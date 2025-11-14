<template>
    <div class="page-box">

        <!-- 筛选 -->
        <el-form :inline="true" class="filter">
            <el-form-item label="标题">
                <el-input v-model="query.subject" placeholder="输入关键字进行筛选" clearable />
            </el-form-item>

            <el-form-item label="部门">
                <el-input v-model="query.departmentName" placeholder="输入关键字进行筛选" clearable />
            </el-form-item>

            <el-form-item label="内容">
                <el-input v-model="query.content" placeholder="输入关键字进行筛选" clearable />
            </el-form-item>

            <!-- 只有收件箱才有“已读”筛选 -->
            <el-form-item v-if="!isRecycle">
                <span class="el-form-item__label">已读</span>
                <el-select
                    v-model="query.isRead"
                    placeholder="全部"
                    clearable
                    style="width: 100px"
                    @change="getPage"
                >
                    <el-option label="全部" :value="null" />
                    <el-option label="未读" :value="0" />
                    <el-option label="已读" :value="1" />
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-switch
                    v-model="query.orderDesc"
                    active-text="倒序"
                    inactive-text="顺序"
                    @change="getPage"
                />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="getPage">搜索</el-button>
                <el-button type="primary" @click="resetFilter">重置</el-button>
            </el-form-item>
        </el-form>

        <el-tooltip content="页面每 30 秒自动刷新一次" placement="right" effect="light">
            <span class="refresh-time">
                上次刷新：{{ lastRefreshText }}
            </span>
        </el-tooltip>

        <!-- 表格 -->
        <el-table
            :data="tableData"
            style="width: 100%"
            height="520"
            :row-class-name="rowClass"
        >
            <el-table-column prop="departmentName" label="部门" width="180" />

            <el-table-column label="标题" width="260">
                <template #default="scope">
                    {{ shortTitle(scope.row.subject) }}
                </template>
            </el-table-column>

            <el-table-column label="内容">
                <template #default="scope">
                    <span class="link" @click="openDetail(scope.row)">
                        {{ shortContent(scope.row.content) }}
                    </span>
                </template>
            </el-table-column>

            <el-table-column label="到达时间" width="200">
                <template #default="scope">
                    {{ formatDate(scope.row.arrivedAt) }}
                </template>
            </el-table-column>

            <el-table-column label="操作" width="260">
                <template #default="scope">
                    <el-button link type="primary" @click="openDetail(scope.row)">查看详情</el-button>

                    <!-- 收件箱：标记已读 / 移入回收站 -->
                    <template v-if="!isRecycle">
                        <el-button
                            link
                            :type="scope.row.isRead ? 'warning' : 'success'"
                            @click="toggleRead(scope.row)"
                        >
                            {{ scope.row.isRead ? '撤销已读' : '标记已读' }}
                        </el-button>

                        <el-button link type="danger" @click="moveToRecycle(scope.row.id)">
                            移入回收站
                        </el-button>
                    </template>

                    <!-- 回收站：撤销删除 / 彻底删除 -->
                    <template v-else>
                        <el-button link type="warning" @click="undoDelete(scope.row.id)">
                            撤销删除
                        </el-button>
                        <el-button link type="danger" @click="removeForever(scope.row.id)">
                            彻底删除
                        </el-button>
                    </template>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="getPage"
        />

        <!-- 详情弹窗 -->
        <el-dialog v-model="dialogVisible" title="邮件详情" width="650px">
            <p><strong>来自：</strong>{{ detail.departmentName }}</p>

            <p style="margin-top: 10px;">
                <strong>附件：</strong>
                <template v-if="attachmentNames.length">
                    <span
                        v-for="(name, idx) in attachmentNames"
                        :key="idx"
                        class="attachment-item-inline"
                        @click="downloadAttachment(idx)"
                    >
                        {{ name }}
                        <span v-if="idx < attachmentNames.length - 1">、</span>
                    </span>
                </template>
                <span v-else>无</span>
            </p>

            <p><strong>标题：</strong>{{ detail.subject }}</p>
            <p><strong>到达时间：</strong>{{ formatDate(detail.arrivedAt) }}</p>

            <p style="margin-top: 15px;"><strong>内容：</strong></p>
            <div class="content-box">{{ detail.content }}</div>
        </el-dialog>

    </div>
</template>

<script setup>
import {
    ref,
    reactive,
    computed,
    onMounted,
    onBeforeUnmount,
    watch
} from 'vue'
import { useRoute } from 'vue-router'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '../../util/format'

// 当前路由：用来区分收件箱 / 回收站
const route = useRoute()
const isRecycle = computed(() => {
    if (typeof route.meta.isRecycle === 'boolean') {
        return route.meta.isRecycle
    }
    return route.path.includes('recycle')
})

// -----------------------
// 基础数据
// -----------------------
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const timer = ref(null)
const lastRefreshText = ref('尚未刷新')

// -----------------------
// 详情 + 附件
// -----------------------
const dialogVisible = ref(false)
const detail = reactive({})
const attachmentNames = ref([])

// -----------------------
// 筛选条件
// -----------------------
const query = reactive({
    subject: '',
    content: '',
    departmentName: '',
    isRead: null,   // 仅收件箱使用
    orderDesc: true
})

// 文字截断
const shortTitle = (str) =>
    !str ? '-' : (str.length > 15 ? str.slice(0, 15) + '...' : str)
const shortContent = (str) =>
    !str ? '-' : (str.length > 30 ? str.slice(0, 30) + '...' : str)


// -----------------------
// 获取分页
// -----------------------
const getPage = async () => {
    const hasFilter = isRecycle.value
        ? !!(query.subject || query.content || query.departmentName)
        : !!(query.subject || query.content || query.departmentName || query.isRead !== null)

    const body = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        isDeleted: isRecycle.value ? 1 : 0,
        pure: !hasFilter,
        orderDesc: query.orderDesc,
        subject: query.subject,
        content: query.content,
        departmentName: query.departmentName
    }
    if (!isRecycle.value) {
        body.isRead = query.isRead
    }

    const { data } = await request.post('/mailbox/page', body)
    if (data.code === 200) {
        tableData.value = data.data.records
        total.value = data.data.total
        lastRefreshText.value = formatDate(new Date())
    }
}

watch(
    () => isRecycle.value,
    () => {
        pageNum.value = 1          // 切 tab 时从第一页开始
        if (isRecycle.value) {
            query.isRead = null
        }
        getPage()
    }
)


// -----------------------
// 打开详情弹窗
// -----------------------
const openDetail = async (row) => {
    // 只有收件箱才在点击时标记已读
    if (!isRecycle.value && row.isRead === 0) {
        await request.post(`/mailbox/read/${row.id}?isRead=1`)
        // 不等后端返回再刷新行，直接整体刷新一次
        getPage()
    }

    Object.assign(detail, row)
    dialogVisible.value = true
}


// -----------------------
// 加载附件名称
// -----------------------
const fetchAttachmentNames = async () => {
    if (!detail.attachments || detail.attachments.length === 0) {
        attachmentNames.value = []
        return
    }

    const { data } = await request.post('/oss/name', {
        urls: detail.attachments
    })

    if (data.code === 200) {
        attachmentNames.value = data.data
    } else {
        attachmentNames.value = []
    }
}

// 监听弹窗打开后触发附件加载
watch(dialogVisible, (v) => {
    if (v) fetchAttachmentNames()
})


// -----------------------
// 下载附件
// -----------------------
const downloadAttachment = (idx) => {
    const url = detail.attachments[idx]
    const name = attachmentNames.value[idx]

    ElMessageBox.confirm(
        `是否下载附件：${name}？`,
        '提示',
        {
            confirmButtonText: '下载',
            cancelButtonText: '取消',
            type: 'info'
        }
    ).then(() => {
        const a = document.createElement('a')
        a.href = url
        a.download = name
        a.target = '_self'
        a.click()
    })
}


// -----------------------
// 收件箱：标记已读 / 移入回收站
// -----------------------
const toggleRead = async (row) => {
    const newValue = row.isRead ? 0 : 1
    await request.post(`/mailbox/read/${row.id}?isRead=${newValue}`)
    ElMessage.success(newValue ? '已标记已读' : '已撤销已读')
    getPage()
}

const moveToRecycle = async (id) => {
    await request.post(`/mailbox/delete/${id}?isDeleted=1`)
    ElMessage.success('已移入回收站')
    getPage()
}


// -----------------------
// 回收站：撤销删除 / 彻底删除
// -----------------------
const undoDelete = async (id) => {
    await request.post(`/mailbox/delete/${id}?isDeleted=0`)
    ElMessage.success('已撤销删除')
    getPage()
}

const removeForever = async (id) => {
    await request.post(`/mailbox/remove/${id}`)
    ElMessage.success('已彻底删除')
    getPage()
}


// 行样式
const rowClass = ({ row }) => (row.isRead ? 'row-read' : 'row-unread')


// -----------------------
// 生命周期
// -----------------------
onMounted(() => {
    getPage()
    timer.value = setInterval(getPage, 30000)
})

onBeforeUnmount(() => {
    clearInterval(timer.value)
})


// -----------------------
// 重置筛选
// -----------------------
const resetFilter = () => {
    query.subject = ''
    query.content = ''
    query.departmentName = ''
    query.isRead = null
    query.orderDesc = true
    getPage()
}
</script>

<style src="@/assets/css/mailbox_page.css"></style>
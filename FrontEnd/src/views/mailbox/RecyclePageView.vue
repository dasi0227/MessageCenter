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

            <el-form-item label="已读">
                <el-select v-model="query.isRead" placeholder="全部" clearable style="width: 100px" @change="getPage">
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
        <el-table :data="tableData" style="width: 100%" height="520" :row-class-name="rowClass">
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
                    <el-button link type="warning" @click="undoDelete(scope.row.id)">撤销删除</el-button>
                    <el-button link type="danger" @click="remove(scope.row.id)">彻底删除</el-button>
                </template>
            </el-table-column>
        </el-table>

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
            <p><strong>标题：</strong>{{ detail.subject }}</p>
            <p><strong>到达时间：</strong>{{ formatDate(detail.arrivedAt) }}</p>
            <p style="margin-top: 15px;"><strong>内容：</strong></p>
            <div class="content-box">{{ detail.content }}</div>

        </el-dialog>

    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import request from '../../api/request'
import { ElMessage } from 'element-plus'
import { formatDate } from '../../util/format'

// 数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const timer = ref(null)
const lastRefreshText = ref('尚未刷新')

const query = reactive({
    subject: '',
    content: '',
    departmentName: '',
    orderDesc: true
})

const shortTitle = (str) => !str ? '-' : (str.length > 15 ? str.slice(0, 15) + '...' : str)
const shortContent = (str) => !str ? '-' : (str.length > 30 ? str.slice(0, 30) + '...' : str)

onMounted(() => {
    getPage()

    timer.value = setInterval(() => {
        getPage()
    }, 30000)
})

onBeforeUnmount(() => {
    clearInterval(timer.value)
})

const getPage = async () => {
    const hasFilter = !!(query.subject || query.content || query.departmentName || query.isRead !== null)

    const { data } = await request.post('/mailbox/page', {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        isDeleted: 1,
        isRead: query.isRead,
        pure: !hasFilter,
        orderDesc: query.orderDesc,
        ...query
    })

    if (data.code === 200) {
        tableData.value = data.data.records
        total.value = data.data.total
        lastRefreshText.value = formatDate(new Date())
    }
}

const resetFilter = () => {
    query.subject = ''
    query.content = ''
    query.departmentName = ''
    query.isRead = null
    query.orderDesc = true
    getPage()
}

const openDetail = async (row) => {
    if (row.isRead === 0) {
        await request.post(`/mailbox/read/${row.id}?isRead=1`)
    }
    Object.assign(detail, row)
    dialogVisible.value = true
    getPage()
}

const undoDelete = async (id) => {
    await request.post(`/mailbox/delete/${id}?isDeleted=0`)
    ElMessage.success('已撤销删除')
    getPage()
}

const remove = async (id) => {
    await request.post(`/mailbox/remove/${id}`)
    ElMessage.success('已彻底删除')
    getPage()
}

const rowClass = ({ row }) => {
    return row.isRead ? 'row-read' : 'row-unread'
}

const dialogVisible = ref(false)
const detail = reactive({})
</script>

<style src="@/assets/css/mailbox_page.css"></style>
<template>
    <div class="dispatch-page">
        <!-- 💡 顶部消息基本信息 -->
        <div class="header">
            <el-page-header @back="router.back" :content="`消息 ID：${messageId}`" />
            <el-card class="message-info" shadow="never">
                <div class="info-item"><strong>账户：</strong>{{ messageInfo.accountName || '—' }}</div>
                <div class="info-item"><strong>部门：</strong>{{ messageInfo.departmentName || '—' }}</div>
                <div class="info-item"><strong>原始标题：</strong>{{ messageInfo.subject || '—' }}</div>
                <div class="info-item"><strong>原始内容：</strong>{{ messageInfo.content || '—' }}</div>
            </el-card>
        </div>

        <!-- 💡 筛选栏 -->
        <div class="toolbar">
            <el-select
                v-model="selectedContact"
                filterable
                clearable
                placeholder="选择联系人"
                style="width: 200px; margin-right: 10px"
            >
                <el-option
                    v-for="item in contactList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                />
            </el-select>

            <el-select
                v-model="selectedStatus"
                filterable
                clearable
                placeholder="选择状态"
                style="width: 150px; margin-right: 10px"
            >
                <el-option
                    v-for="s in statusList"
                    :key="s"
                    :label="s"
                    :value="s"
                />
            </el-select>

            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="contactName" label="联系人" width="160" />
            <el-table-column prop="target" label="目标地址" width="200" />
            
            <!-- 💡 状态彩色标签 -->
            <el-table-column label="状态" width="140">
                <template #default="{ row }">
                    <span
                        class="status-tag"
                        :class="{
                            success: row.status === 'SUCCESS',
                            fail: row.status === 'FAIL',
                            sending: row.status === 'SENDING',
                            pending: row.status === 'PENDING'
                        }"
                    >
                        {{ row.status }}
                    </span>
                </template>
            </el-table-column>

            <el-table-column prop="errorMsg" label="错误信息" show-overflow-tooltip>
                <template #default="{ row }">{{ row.errorMsg || '—' }}</template>
            </el-table-column>

            <el-table-column
                prop="sentAt"
                label="发送时间"
                :formatter="(_, __, v) => formatDate(v)"
                width="180"
            />
            <el-table-column
                prop="finishedAt"
                label="完成时间"
                :formatter="(_, __, v) => formatDate(v)"
                width="180"
            />
            <!-- 💡 查看内容按钮 -->
            <el-table-column label="操作" width="120">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="openDetail(row)">
                        查看内容
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination
                background
                layout="prev, pager, next, jumper"
                :total="total"
                :page-size="pageSize"
                v-model:current-page="pageNum"
                @current-change="getPage"
            />
        </div>

        <!-- 💡 弹窗：查看标题和内容 -->
        <el-dialog v-model="detailVisible" title="消息内容" width="600px" align-center>
            <el-form label-width="80px">
                <el-form-item label="标题">
                    <el-input v-model="detail.subject" readonly />
                </el-form-item>
                <el-form-item label="内容">
                    <el-input type="textarea" v-model="detail.content" :rows="8" readonly />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="detailVisible = false">关闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'
import { formatDate } from '../utils/format'

const router = useRouter()
const route = useRoute()

const messageId = route.params.messageId

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 💡 筛选条件
const selectedContact = ref(null)
const selectedStatus = ref(null)
const contactList = ref([])
const statusList = ref([])

// 💡 详情弹窗
const detailVisible = ref(false)
const detail = ref({ subject: '', content: '' })

// 💡 顶部消息信息
const messageInfo = ref({})

// 加载下拉数据
const initOptions = async () => {
    try {
        const [contactRes, statusRes, msgInfoRes] = await Promise.all([
            request.get('/contact/list'),
            request.get('/message/status/list'),
            request.post('/message/page', { pageNum: 1, pageSize: 1, messageId })
        ])
        if (contactRes.data.code === 200) contactList.value = contactRes.data.data
        if (statusRes.data.code === 200) statusList.value = statusRes.data.data
        if (msgInfoRes.data.code === 200 && msgInfoRes.data.data.records.length > 0) {
            messageInfo.value = msgInfoRes.data.data.records[0]
        }
    } catch {
        ElMessage.error('初始化数据失败')
    }
}

// 查询派送详情
const getPage = async () => {
    try {
        const { data } = await request.post('/message/detail', {
            messageId,
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            status: selectedStatus.value,
            contactId: selectedContact.value,
        })
        if (data.code === 200) {
            tableData.value = data.data.records
            total.value = data.data.total
        } else {
            ElMessage.error(data.msg || '加载失败')
        }
    } catch {
        ElMessage.error('请求失败，请检查接口')
    }
}

// 筛选
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}

// 重置
const resetFilters = () => {
    selectedContact.value = null
    selectedStatus.value = null
    getPage()
}

// 💡 打开内容详情弹窗
const openDetail = (row) => {
    detail.value = { subject: row.subject, content: row.content }
    detailVisible.value = true
}

onMounted(async () => {
    await initOptions()
    await getPage()
})
</script>

<style scoped>
.dispatch-page {
    padding: 20px;
}
.header {
    margin-bottom: 20px;
}
.message-info {
    margin-top: 10px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 6px;
}
.info-item {
    line-height: 1.6;
}
.toolbar {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    flex-wrap: wrap;
}
.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
.status-tag {
    padding: 4px 10px;
    border-radius: 6px;
    color: #fff;
    font-weight: 500;
}
.status-tag.success {
    background-color: #67c23a;
}
.status-tag.fail {
    background-color: #f56c6c;
}
.status-tag.sending {
    background-color: #e6a23c;
}
.status-tag.pending {
    background-color: #909399;
}
</style>
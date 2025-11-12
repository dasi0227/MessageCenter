<template>
    <div class="dispatch-page">
        <!-- 顶部消息基本信息 -->
        <div class="header">
            <el-page-header @back="router.back" :content="`消息 ID：${messageId}`" />
            <el-card class="message-info" shadow="never">
                <div class="info-item"><strong>账户：</strong>{{ messageInfo.accountName || '—' }}</div>
                <div class="info-item"><strong>部门：</strong>{{ messageInfo.departmentName || '—' }}</div>
                <div class="info-item"><strong>原始标题：</strong>{{ messageInfo.subject || '—' }}</div>
                <div class="info-item truncate-content">
                    <strong>原始内容：</strong>
                    <span>{{ messageInfo.content || '—' }}</span>
                </div>
            </el-card>
        </div>

        <!-- 筛选栏 -->
        <div class="toolbar">
            <el-select
                v-model="selectedContact"
                filterable
                clearable
                placeholder="选择联系人"
                style="width: 200px; margin-right: 10px"
            >
                <el-option v-for="item in contactList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>

            <el-select
                v-model="selectedStatus"
                filterable
                clearable
                placeholder="选择状态"
                style="width: 150px; margin-right: 10px"
            >
                <el-option v-for="s in statusList" :key="s" :label="s" :value="s" />
            </el-select>

            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="contactName" label="联系人" width="100" />
            <el-table-column prop="target" label="目标地址" width="200" />
            <el-table-column label="状态" width="120">
                <template #default="{ row }">
                    <span
                        class="status-tag"
                        :class="{
                            success: row.status === 'SUCCESS',
                            fail: row.status === 'FAIL',
                            sending: row.status === 'SENDING',
                            pending: row.status === 'PENDING',
                            error: row.status === 'ERROR',
                        }"
                    >
                        {{ row.status }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="errorMsg" label="错误信息" min-width="400" show-overflow-tooltip>
                <template #default="{ row }">{{ row.errorMsg || '—' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="160">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="openDetail(row)">
                        查看详情
                    </el-button>
                    <el-button type="primary" size="small" @click="resend(row)">
                        重发
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

        <!-- 弹窗：查看详情 -->
        <el-dialog v-model="detailVisible" title="派送详情" width="700px" align-center>
            <el-descriptions :column="1" border>
                <el-descriptions-item label="联系人">{{ detail.contactName || '—' }}</el-descriptions-item>
                <el-descriptions-item label="目标地址">{{ detail.target || '—' }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <span
                        class="status-tag"
                        :class="{
                            success: detail.status === 'SUCCESS',
                            fail: detail.status === 'FAIL',
                            sending: detail.status === 'SENDING',
                            pending: detail.status === 'PENDING',
                            error: detail.status === 'ERROR',
                        }"
                    >
                        {{ detail.status }}
                    </span>
                </el-descriptions-item>
                <el-descriptions-item label="错误信息">{{ detail.errorMsg || '—' }}</el-descriptions-item>
                <el-descriptions-item label="发送时间">{{ formatDate(detail.sentAt) || '—' }}</el-descriptions-item>
                <el-descriptions-item label="完成时间">{{ formatDate(detail.finishedAt) || '—' }}</el-descriptions-item>
                <el-descriptions-item label="标题">{{ detail.subject || '—' }}</el-descriptions-item>
                <el-descriptions-item label="内容">
                    <div style="white-space: pre-wrap;">{{ detail.content || '—' }}</div>
                </el-descriptions-item>
            </el-descriptions>
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

// 筛选条件
const selectedContact = ref(null)
const selectedStatus = ref(null)
const contactList = ref([])
const statusList = ref([])

// 详情弹窗
const detailVisible = ref(false)
const detail = ref({})

// 顶部消息信息
const messageInfo = ref({})

// 初始化选项
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

// 重发消息
const resend = (row) => {
    router.push({
        path: '/send',
        query: {
            fromResend: '1',
            departmentId: messageInfo.value.departmentId,
            departmentName: messageInfo.value.departmentName,
            channel: messageInfo.value.channel,
            subject: row.subject || messageInfo.value.subject,
            content: row.content || messageInfo.value.content,
            contactIds: JSON.stringify([row.contactId]),
        },
    })
}

// 搜索与重置
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}
const resetFilters = () => {
    selectedContact.value = null
    selectedStatus.value = null
    getPage()
}

// 打开详情弹窗
const openDetail = (row) => {
    detail.value = { ...row }
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
.truncate-content span {
    display: -webkit-box;
    -webkit-line-clamp: 3;       /* 限制显示 3 行 */
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: normal;
    word-break: break-all;
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
.status-tag.error {
    background-color: #dc2626;
}
.status-tag.sending {
    background-color: #e6a23c;
}
.status-tag.pending {
    background-color: #909399;
}
:deep(.el-descriptions__label) {
    width: 120px !important; /* 固定标签宽度 */
    flex: 0 0 120px !important;
}
:deep(.el-descriptions__cell) {
    align-items: flex-start; /* 内容顶对齐更自然 */
}
:deep(.el-dialog__body) {
    max-height: 60vh; /* 弹窗内容最大高度 */
    overflow-y: auto; /* 弹窗内滚动 */
    padding-right: 10px;
}
</style>

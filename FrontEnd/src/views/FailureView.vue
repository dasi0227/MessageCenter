<template>
    <div class="failure-page">
        <!-- 筛选栏 -->
        <div class="toolbar">
            <el-input
                v-model="query.errorType"
                placeholder="错误类型"
                clearable
                style="width: 180px; margin-right: 10px"
            />
            <el-input
                v-model="query.errorMessage"
                placeholder="错误信息"
                clearable
                style="width: 200px; margin-right: 10px"
            />
            <el-select
                v-model="query.status"
                placeholder="选择状态"
                clearable
                style="width: 150px; margin-right: 10px"
            >
                <el-option v-for="s in statusList" :key="s" :label="s" :value="s" />
            </el-select>
            <el-date-picker
                v-model="query.timeRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                style="margin-right: 10px"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="dispatchId" label="分发 ID" width="150" />
            <el-table-column prop="errorType" label="错误类型" width="180" />
            <el-table-column prop="errorMessage" label="错误信息" min-width="300" show-overflow-tooltip />
            <el-table-column label="状态" width="140">
                <template #default="{ row }">
                    <span class="status-tag" :class="row.status?.toLowerCase()">{{ row.status }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="300">
                <template #default="{ row }">
                    <el-button size="small" type="primary" @click="openDetail(row)">查看详情</el-button>
                    <el-button size="small" type="warning" @click="updateStatus(row, 'IGNORED')">忽略</el-button>
                    <template v-if="row.status === 'PROCESSING' || row.status === 'RESOLVED'">
                        <el-button size="small" type="danger" @click="updateStatus(row, 'UNHANDLED')">恢复</el-button>
                    </template>
                    <template v-else>
                        <el-button size="small" type="danger" @click="updateStatus(row, 'PROCESSING')">处理</el-button>
                    </template>
                    <el-button size="small" type="success" @click="updateStatus(row, 'RESOLVED')">解决</el-button>
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

        <!-- 弹窗：错误详情 -->
        <el-dialog v-model="detailVisible" title="错误详情" width="850px" align-center class="detail-dialog">
            <el-descriptions :column="1" border class="detail-descriptions">
                <el-descriptions-item label="分发 ID">{{ detail.dispatchId || '—' }}</el-descriptions-item>
                <el-descriptions-item label="错误类型">{{ detail.errorType || '—' }}</el-descriptions-item>
                <el-descriptions-item label="错误信息">{{ detail.errorMessage || '—' }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <span class="status-tag" :class="detail.status?.toLowerCase()">
                        {{ detail.status }}
                    </span>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDate(detail.createdAt) || '—' }}</el-descriptions-item>
                <el-descriptions-item label="解决时间">{{ formatDate(detail.resolvedAt) || '—' }}</el-descriptions-item>
                <el-descriptions-item label="错误栈">{{ detail.errorStack || '—' }}</el-descriptions-item>
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
import request from '../api/request'
import { ElMessage } from 'element-plus'
import { formatDate } from '../util/format'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const query = ref({
    errorType: '',
    errorMessage: '',
    status: null,
    timeRange: []
})

const statusList = ref([])
const detailVisible = ref(false)
const detail = ref({})

// 初始化状态列表
const initOptions = async () => {
    try {
        const { data } = await request.get('/failure/status/list')
        if (data.code === 200) statusList.value = data.data
    } catch {
        ElMessage.error('加载状态列表失败')
    }
}

// 获取分页数据
const getPage = async () => {
    try {
        const hasFilter = !!(
            query.value.errorType ||
            query.value.errorMessage ||
            query.value.status ||
            (query.value.timeRange && query.value.timeRange.length > 0)
        )

        const { data } = await request.post('/failure/page', {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            errorType: query.value.errorType || null,
            errorMessage: query.value.errorMessage || null,
            status: query.value.status || null,
            startTime: query.value.timeRange?.[0] || null,
            endTime: query.value.timeRange?.[1] || null,
            pure: !hasFilter
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

// 更新状态
const updateStatus = async (row, status) => {
    try {
        const { data } = await request.post('/failure/status', { id: row.id, status })
        if (data.code === 200) {
            ElMessage.success('状态更新成功')
            getPage()
        } else {
            ElMessage.error(data.msg || '更新失败')
        }
    } catch {
        ElMessage.error('请求异常')
    }
}

// 查看详情
const openDetail = (row) => {
    detail.value = { ...row }
    detailVisible.value = true
}

// 搜索与重置
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}
const resetFilters = () => {
    query.value = { errorType: '', errorMessage: '', status: null, timeRange: [] }
    getPage()
}

onMounted(async () => {
    await initOptions()
    await getPage()
})
</script>

<style scoped>
.failure-page {
    padding: 20px;
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

/* 状态标签样式 */
.status-tag {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 6px;
    font-weight: 500;
    font-size: 13px;
    color: #fff;
    text-align: center;
    min-width: 80px;
}
.status-tag.unhandled {
    background-color: #909399; /* 未处理：灰 */
}
.status-tag.processing {
    background-color: #e6a23c; /* 处理中：橙 */
}
.status-tag.resolved {
    background-color: #67c23a; /* 已解决：绿 */
}
.status-tag.ignored {
    background-color: #f56c6c; /* 已忽略：红 */
}
/* 弹窗整体可滚动 */
:deep(.detail-dialog .el-dialog__body) {
    max-height: 75vh;
    overflow-y: auto;
}
:deep(.detail-descriptions .el-descriptions__label) {
    width: 120px !important;
    flex: 0 0 120px !important;
    white-space: nowrap;
}
</style>
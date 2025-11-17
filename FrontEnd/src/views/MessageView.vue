<template>
    <div class="message-page">
        <!-- 顶部筛选栏 -->
        <div class="toolbar">
            <!-- 账户 -->
            <el-select
                v-model="selectedAccount"
                value-key="id"
                filterable
                clearable
                placeholder="选择账户"
                style="width: 120px; margin-right: 10px"
            >
                <el-option
                    v-for="item in accountList"
                    :key="item.id"
                    :label="item.name"
                    :value="item"
                />
            </el-select>

            <!-- 部门 -->
            <el-select
                v-model="selectedDepartment"
                value-key="id"
                filterable
                clearable
                placeholder="选择部门"
                style="width: 160px; margin-right: 10px"
            >
                <el-option
                    v-for="item in departmentList"
                    :key="item.id"
                    :label="item.name"
                    :value="item"
                />
            </el-select>

            <!-- 渠道 -->
            <el-select
                v-model="selectedChannel"
                filterable
                clearable
                placeholder="选择渠道"
                style="width: 120px; margin-right: 10px"
            >
                <el-option
                    v-for="c in channelList"
                    :key="c"
                    :label="c"
                    :value="c"
                />
            </el-select>

            <!-- 标题 -->
            <el-input
                v-model="subject"
                placeholder="标题筛选"
                clearable
                style="width: 200px; margin-right: 10px"
            />

            <!-- 时间区间 -->
            <el-date-picker
                v-model="timeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 200px; margin-right: 10px"
            />

            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="primary" @click="resetFilters">重置</el-button>
        </div>

        <!-- 消息表格 -->
        <el-table
            :data="tableData"
            stripe
            border
            fit
            style="width: 100%; margin-top: 20px"
        >
            <el-table-column prop="accountName" label="账户" min-width="100" />
            <el-table-column prop="departmentName" label="部门" min-width="120" />
            <el-table-column prop="channel" label="渠道" min-width="100" />
            <el-table-column prop="subject" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="contactNames" label="联系人" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                    <span>{{ row.contactNames || '—' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180"
                            :formatter="(_, __, value) => formatDate(value)" />
            <el-table-column label="操作" width="120">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="viewDetail(scope.row)">
                        查看详情
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
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { formatDate } from '../util/format'

const router = useRouter()

// ------------------ 搜索条件 ------------------
const selectedAccount = ref(null)
const selectedDepartment = ref(null)
const selectedChannel = ref(null)
const subject = ref('')
const timeRange = ref([])

// ------------------ 下拉数据 ------------------
const accountList = ref([])
const departmentList = ref([])
const channelList = ref([])

// ------------------ 分页 ------------------
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// ------------------ 接口调用 ------------------
const contactNameMap = ref({})

// 初始化加载一次所有数据
const initOptions = async () => {
    const [accRes, depRes, chRes, contactRes] = await Promise.all([
        request.get('/account/list'),
        request.get('/department/list'),
        request.get('/message/channel/list'),
        request.get('/contact/list'),
    ])

    if (accRes.data.code === 200) accountList.value = accRes.data.data
    if (depRes.data.code === 200) departmentList.value = depRes.data.data
    if (chRes.data.code === 200) channelList.value = chRes.data.data
    if (contactRes.data.code === 200) {
        contactNameMap.value = Object.fromEntries(
            contactRes.data.data.map((c) => [c.id, c.name])
        )
    }
}

// 分页获取消息
const getPage = async () => {
    const [startTime, endTime] = timeRange.value || []
    const hasFilter = !!(
        subject.value ||
        selectedAccount.value ||
        selectedDepartment.value ||
        selectedChannel.value ||
        startTime ||
        endTime
    )
    const { data } = await request.post('/message/page', {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        subject: subject.value,
        accountId: selectedAccount.value?.id,
        departmentId: selectedDepartment.value?.id,
        channel: selectedChannel.value,
        startTime,
        endTime,
        pure: !hasFilter
    })
    if (data.code === 200) {
        tableData.value = data.data.records.map((m) => {
            const names =
                Array.isArray(m.contactIds) && m.contactIds.length > 0
                    ? m.contactIds.map((id) => contactNameMap.value[id] || `未知(${id})`).join(', ')
                    : '—'
            return { ...m, contactNames: names }
        })
        total.value = data.data.total
    }
}

// 搜索
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}

// 重置筛选条件
const resetFilters = () => {
    selectedAccount.value = null
    selectedDepartment.value = null
    selectedChannel.value = null
    subject.value = ''
    timeRange.value = []
    pageNum.value = 1
    getPage()
}

// 查看详情
const viewDetail = (row) => {
  router.push({
    path: `/dispatch/${row.id}`
  })
}

onMounted(async () => {
    await initOptions()
    await getPage()
})
</script>

<style scoped>
.message-page {
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
</style>
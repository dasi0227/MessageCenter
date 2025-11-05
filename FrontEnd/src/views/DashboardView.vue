<template>
    <div>
        <div class="filter-bar">
            <el-input v-model="filters.keyword" placeholder="请输入关键词" clearable style="width: 200px;" />
            <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width: 160px; margin-left: 10px;">
                <el-option label="已读" value="read" />
                <el-option label="未读" value="unread" />
            </el-select>
            <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">搜索</el-button>
        </div>

        <el-table :data="tableData" border stripe style="margin-top: 20px;">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="createdAt" label="创建时间" width="180" />
        </el-table>

        <div class="pagination">
            <el-pagination background layout="prev, pager, next" :page-size="10" :total="tableData.length" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const filters = ref({ keyword: '', status: '' })
const tableData = ref([
    { id: 1, title: '系统通知', status: '已读', createdAt: '2025-11-05 10:00' },
    { id: 2, title: '任务提醒', status: '未读', createdAt: '2025-11-05 11:30' },
])
const handleSearch = () => {
    ElMessage.success(`搜索：关键词=${filters.value.keyword || '无'}, 状态=${filters.value.status || '全部'}`)
}
</script>

<style scoped>
.filter-bar { display: flex; align-items: center; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
</style>
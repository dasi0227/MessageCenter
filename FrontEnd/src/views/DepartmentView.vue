<template>
    <div class="department-page">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
            <el-input
                v-model="name"
                placeholder="部门名称筛选"
                style="width: 200px; margin-right: 10px"
                clearable
            />
            <el-input
                v-model="address"
                placeholder="部门地址筛选"
                style="width: 200px; margin-right: 10px"
                clearable
            />
            <el-input
                v-model="description"
                placeholder="部门介绍筛选"
                style="width: 200px; margin-right: 10px"
                clearable
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="primary" @click="resetFilters">重置</el-button>
            <el-button type="success" @click="handleAdd">新增部门</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="name" label="部门名称" width="150" />
            <el-table-column prop="phone" label="手机号" width="160" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="address" label="地址" width="220" />
            <el-table-column prop="description" label="介绍" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="180">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="handleEdit(scope.row)">修改</el-button>
                    <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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

        <!-- 修改弹窗 -->
        <el-dialog v-model="editVisible" title="修改部门信息" width="500px">
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="名称" required>
                    <el-input v-model="editForm.name" placeholder="请输入部门名称" />
                </el-form-item>
                <el-form-item label="地址" required>
                    <el-input v-model="editForm.address" placeholder="请输入部门地址" />
                </el-form-item>
                <el-form-item label="介绍" required>
                    <el-input
                        v-model="editForm.description"
                        type="textarea"
                        rows="3"
                        placeholder="请输入部门介绍"
                    />
                </el-form-item>
                <el-form-item label="手机号" required>
                    <el-input v-model="editForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" required>
                    <el-input v-model="editForm.email" placeholder="请输入邮箱" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editVisible = false">取消</el-button>
                <el-button type="primary" @click="submitEdit">保存</el-button>
            </template>
        </el-dialog>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增部门" width="500px">
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="名称" required>
                    <el-input v-model="addForm.name" placeholder="请输入部门名称" />
                </el-form-item>
                <el-form-item label="地址" required>
                    <el-input v-model="addForm.address" placeholder="请输入部门地址" />
                </el-form-item>
                <el-form-item label="介绍" required>
                    <el-input
                        v-model="addForm.description"
                        type="textarea"
                        rows="3"
                        placeholder="请输入部门介绍"
                    />
                </el-form-item>
                <el-form-item label="手机号" required>
                    <el-input v-model="addForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" required>
                    <el-input v-model="addForm.email" placeholder="请输入邮箱" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="addVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAdd">新增</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 筛选字段
const name = ref('')
const address = ref('')
const description = ref('')

// 数据分页
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 弹窗
const editVisible = ref(false)
const addVisible = ref(false)

// 表单
const editForm = ref({ id: null, name: '', address: '', description: '', phone: '', email: '' })
const addForm = ref({ name: '', address: '', description: '', phone: '', email: '' })

// 分页查询
const resetFilters = () => {
    name.value = ''
    address.value = ''
    description.value = ''
    pageNum.value = 1
    getPage()
}

const getPage = async () => {
    try {
        const hasFilter = !!(name.value || address.value || description.value)
        const { data } = await request.post('/department/page', {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            name: name.value,
            address: address.value,
            description: description.value,
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

// 搜索
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}

// 删除
const handleDelete = (row) => {
    ElMessageBox.confirm(`确定要删除部门「${row.name}」吗？`, '提示', {
        type: 'warning'
    })
        .then(async () => {
            const { data } = await request.post(`/department/remove/${row.id}`)
            if (data.code === 200) {
                ElMessage.success('删除成功')
                getPage()
            } else {
                ElMessage.error(data.msg || '删除失败')
            }
        })
        .catch(() => {})
}

// 修改
const handleEdit = (row) => {
    editForm.value = { ...row }
    editVisible.value = true
}

const submitEdit = async () => {
    const { data } = await request.post('/department/update', editForm.value)
    if (data.code === 200) {
        ElMessage.success('修改成功')
        editVisible.value = false
        getPage()
    } else {
        ElMessage.error(data.msg || '修改失败')
    }
}

// 新增
const handleAdd = () => {
    addForm.value = { name: '', address: '', description: '', phone: '', email: '' }
    addVisible.value = true
}

const submitAdd = async () => {
    const { data } = await request.post('/department/add', addForm.value)
    if (data.code === 200) {
        ElMessage.success('新增成功')
        addVisible.value = false
        getPage()
    } else {
        ElMessage.error(data.msg || '新增失败')
    }
}

// 初始化
onMounted(() => {
    getPage()
})
</script>

<style scoped>
.department-page {
    padding: 20px;
}
.toolbar {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 10px;
}
.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
</style>
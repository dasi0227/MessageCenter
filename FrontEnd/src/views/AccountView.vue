<template>
    <div class="account-page">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
            <el-input
                v-model="name"
                placeholder="输入账户名筛选"
                style="width: 240px; margin-right: 10px"
                clearable
            />
            <el-select v-model="selectedRole" placeholder="角色筛选" clearable style="width: 160px; margin-right: 10px">
                <el-option v-for="r in roleList" :key="r" :label="r" :value="r" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">新增账户</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="name" label="账户名" />
            <el-table-column prop="role" label="角色" />
            <el-table-column prop="createdAt" label="创建时间" :formatter="(_, __, value) => formatDate(value)" />
            <el-table-column prop="updatedAt" label="更新时间" :formatter="(_, __, value) => formatDate(value)" />
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
        <el-dialog v-model="editVisible" title="修改账户" width="400px">
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="账户名">
                    <el-input v-model="editForm.name" placeholder="请输入账户名" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="editForm.password" placeholder="请输入新密码（可选）" show-password />
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="editForm.role" placeholder="请选择角色">
                        <el-option v-for="r in roleList" :key="r" :label="r" :value="r" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editVisible = false">取消</el-button>
                <el-button type="primary" @click="submitEdit">保存</el-button>
            </template>
        </el-dialog>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增账户" width="400px">
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="账户名">
                    <el-input v-model="addForm.name" placeholder="请输入账户名" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="addForm.password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="addForm.role" placeholder="请选择角色">
                        <el-option v-for="r in roleList" :key="r" :label="r" :value="r" />
                    </el-select>
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
import { formatDate } from '../util/format'

const name = ref('')
const selectedRole = ref('')
const roleList = ref([])

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const editVisible = ref(false)
const addVisible = ref(false)
const editForm = ref({ id: null, name: '', password: '' })
const addForm = ref({ name: '', password: '', role: '' })

// 分页查询
const getPage = async () => {
    try {
        const hasFilter = !!(name.value || selectedRole.value)

        const { data } = await request.post('/account/page', {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            name: name.value,
            role: selectedRole.value,
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

// 角色列表
const getRoles = async () => {
    const { data } = await request.get('/account/role')
    if (data.code === 200) roleList.value = data.data
}

// 搜索
const handleSearch = () => {
    pageNum.value = 1
    getPage()
}

// 删除
const handleDelete = (row) => {
    ElMessageBox.confirm(`确定要删除账户「${row.name}」吗？`, '提示', {
        type: 'warning'
    })
        .then(async () => {
            const { data } = await request.post(`/account/remove/${row.id}`)
            if (data.code === 200) {
                ElMessage.success('删除成功')
                getPage()
            } else {
                ElMessage.error(data.msg || '删除失败')
            }
        })
        .catch(() => {})
}

// 修改账户
const handleEdit = (row) => {
    editForm.value = { id: row.id, name: row.name, password: '', role: row.role }
    editVisible.value = true
}

const submitEdit = async () => {
    const { data } = await request.post('/account/update', editForm.value)
    if (data.code === 200) {
        ElMessage.success('修改成功')
        editVisible.value = false
        getPage()
    } else {
        ElMessage.error(data.msg || '修改失败')
    }
}

// 新增账户
const handleAdd = () => {
    addForm.value = { name: '', password: '', role: '' }
    addVisible.value = true
}

const submitAdd = async () => {
    const { data } = await request.post('/account/add', addForm.value)
    if (data.code === 200) {
        ElMessage.success('新增成功')
        addVisible.value = false
        getPage()
    } else {
        ElMessage.error(data.msg || '新增失败')
    }
}

onMounted(() => {
    getRoles()
    getPage()
})
</script>

<style scoped>
.account-page {
    padding: 20px;
}

.toolbar {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
</style>
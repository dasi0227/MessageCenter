<template>
    <div class="contact-page">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
            <el-input v-model="name" placeholder="输入姓名筛选" clearable style="width: 150px; margin-right: 10px" />
            <el-input v-model="phone" placeholder="输入手机号筛选" clearable style="width: 150px; margin-right: 10px" />
            <el-input v-model="email" placeholder="输入邮箱筛选" clearable style="width: 200px; margin-right: 10px" />
            <el-select v-model="status" placeholder="状态筛选" clearable style="width: 120px; margin-right: 10px">
                <el-option label="活跃" :value="1" />
                <el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">新增联系人</el-button>
        </div>

        <!-- 数据表格 -->
        <el-table :data="tableData" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="name" label="姓名" width="80" />
            <el-table-column prop="inbox" label="站内信箱号" width="100" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="createdAt" label="创建时间" width="200" :formatter="(_, __, v) => formatDate(v)" />
            <el-table-column prop="updatedAt" label="更新时间" width="200" :formatter="(_, __, v) => formatDate(v)" />

            <el-table-column label="操作">
                <template #default="{ row }">
                    <!-- 状态开关 -->
                    <el-switch
                        v-model="row.status"
                        :active-value="1"
                        :inactive-value="0"
                        :loading="loadingIds.has(row.id)"
                        :disabled="loadingIds.has(row.id)"
                        inline-prompt
                        active-text="✓"
                        inactive-text="✕"
                        style="--el-switch-on-color: #409EFF; --el-switch-off-color: #dcdfe6; margin-right: 10px"
                        @change="onStatusChange(row)"
                    />
                    <!-- 修改与删除按钮 -->
                    <el-button type="primary" size="small" @click="handleEdit(row)">修改</el-button>
                    <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        <el-dialog v-model="editVisible" title="修改联系人" width="500px">
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="姓名">
                    <el-input v-model="editForm.name" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="editForm.password" placeholder="可留空不修改" show-password />
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="editForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="editForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="editForm.status" placeholder="请选择状态">
                        <el-option label="活跃" :value="1" />
                        <el-option label="禁用" :value="0" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editVisible = false">取消</el-button>
                <el-button type="primary" @click="submitEdit">保存</el-button>
            </template>
        </el-dialog>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增联系人" width="500px">
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="姓名">
                    <el-input v-model="addForm.name" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="addForm.password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="addForm.phone" placeholder="请输入手机号（可选）" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="addForm.email" placeholder="请输入邮箱（可选）" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="addForm.status" placeholder="请选择状态">
                        <el-option label="活跃" :value="1" />
                        <el-option label="禁用" :value="0" />
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
const phone = ref('')
const email = ref('')
const status = ref(null)

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const editVisible = ref(false)
const addVisible = ref(false)
const loadingIds = ref(new Set()) // 行级loading集合

const editForm = ref({ id: null, name: '', password: '', phone: '', email: '', status: 1 })
const addForm = ref({ name: '', password: '', phone: '', email: '', status: 1 })

// 分页查询
const getPage = async () => {
    try {
        const hasFilter =
            (name.value && name.value.trim() !== '') ||
            (phone.value && phone.value.trim() !== '') ||
            (email.value && email.value.trim() !== '') ||
            status.value !== null && status.value !== undefined

        const { data } = await request.post('/contact/page', {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            name: name.value,
            phone: phone.value,
            email: email.value,
            status: status.value,
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
    ElMessageBox.confirm(`确定要删除联系人「${row.name}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
            const { data } = await request.post(`/contact/remove/${row.id}`)
            if (data.code === 200) {
                ElMessage.success('删除成功')
                getPage()
            } else {
                ElMessage.error(data.msg || '删除失败')
            }
        })
        .catch(() => {})
}

// 状态开关（乐观更新 + 失败回滚）
const onStatusChange = async (row) => {
    const prev = row.status === 1 ? 0 : 1 // 变更前的状态
    loadingIds.value.add(row.id)
    try {
        const { data } = await request.post('/contact/status', { id: row.id, status: row.status })
        if (data.code === 200) {
            ElMessage.success(`已${row.status === 1 ? '启用' : '禁用'}「${row.name}」`)
        } else {
            row.status = prev // 回滚
            ElMessage.error(data.msg || '状态更新失败')
        }
    } catch {
        row.status = prev // 回滚
        ElMessage.error('请求失败，已回滚状态')
    } finally {
        loadingIds.value.delete(row.id)
    }
}

// 修改
const handleEdit = (row) => {
    editForm.value = {
        id: row.id,
        name: row.name,
        password: '',
        phone: row.phone,
        email: row.email,
        status: row.status
    }
    editVisible.value = true
}
const submitEdit = async () => {
    const { data } = await request.post('/contact/update', editForm.value)
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
    addForm.value = { name: '', password: '', phone: '', email: '', status: 1 }
    addVisible.value = true
}
const submitAdd = async () => {
    const { data } = await request.post('/contact/add', addForm.value)
    if (data.code === 200) {
        ElMessage.success('新增成功')
        addVisible.value = false
        getPage()
    } else {
        ElMessage.error(data.msg || '新增失败')
    }
}

onMounted(() => {
    getPage()
})
</script>

<style scoped>
.contact-page { padding: 20px; }
.toolbar { display: flex; align-items: center; flex-wrap: wrap; margin-bottom: 10px; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
</style>
<template>
    <div class="template-page">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
            <el-button type="primary" @click="handleAdd">新增模版</el-button>
        </div>

        <!-- 模版卡片区 -->
        <div class="template-grid">
            <el-card
                v-for="tpl in templateList"
                :key="tpl.id"
                class="template-card"
                shadow="hover"
                @click="openDetail(tpl)"
            >
                <div class="tpl-name">{{ tpl.name }}</div>
                <div class="tpl-subject">{{ tpl.subject }}</div>
                <div class="tpl-content">{{ tpl.content }}</div>
                <el-icon class="delete-btn" @click.stop="handleDelete(tpl)">
                    <Close />
                </el-icon>
            </el-card>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增模版" width="500px" align-center>
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="名称">
                    <el-input v-model="addForm.name" placeholder="请输入模版名称" />
                </el-form-item>
                <el-form-item label="标题">
                    <el-input v-model="addForm.subject" placeholder="请输入标题" />
                </el-form-item>
                <el-form-item label="内容">
                    <el-input
                        type="textarea"
                        v-model="addForm.content"
                        :rows="6"
                        placeholder="请输入模版内容"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="addVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitAdd">提交</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 查看 / 编辑弹窗 -->
        <el-dialog v-model="editVisible" title="模版详情" width="600px" align-center>
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="名称">
                    <el-input v-model="editForm.name" />
                </el-form-item>
                <el-form-item label="标题">
                    <el-input v-model="editForm.subject" />
                </el-form-item>
                <el-form-item label="内容">
                    <el-input
                        type="textarea"
                        v-model="editForm.content"
                        :rows="8"
                        style="white-space: pre-wrap"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="editVisible = false">关闭</el-button>
                    <el-button type="primary" @click="submitEdit">保存修改</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

const templateList = ref([])
const addVisible = ref(false)
const editVisible = ref(false)

const addForm = ref({ name: '', subject: '', content: '' })
const editForm = ref({ id: null, name: '', subject: '', content: '' })

// 获取列表
const getList = async () => {
    const { data } = await request.get('/template/list')
    if (data.code === 200) templateList.value = data.data
    else ElMessage.error(data.msg || '加载失败')
}

// 打开详情 / 编辑
const openDetail = (tpl) => {
    editForm.value = { ...tpl }
    editVisible.value = true
}

// 提交修改
const submitEdit = async () => {
    const { data } = await request.post('/template/update', editForm.value)
    if (data.code === 200) {
        ElMessage.success('修改成功')
        editVisible.value = false
        getList()
    } else {
        ElMessage.error(data.msg || '修改失败')
    }
}

// 删除
const handleDelete = (tpl) => {
    ElMessageBox.confirm(`确定要删除模版「${tpl.name}」吗？`, '提示', {
        type: 'warning'
    })
        .then(async () => {
            const { data } = await request.post(`/template/remove/${tpl.id}`)
            if (data.code === 200) {
                ElMessage.success('删除成功')
                getList()
            } else {
                ElMessage.error(data.msg || '删除失败')
            }
        })
        .catch(() => {})
}

// 新增
const handleAdd = () => {
    addForm.value = { name: '', subject: '', content: '' }
    addVisible.value = true
}

// 提交新增
const submitAdd = async () => {
    if (!addForm.value.name || !addForm.value.subject || !addForm.value.content)
        return ElMessage.warning('请填写完整信息')

    const { data } = await request.post('/template/add', addForm.value)
    if (data.code === 200) {
        ElMessage.success('新增成功')
        addVisible.value = false
        getList()
    } else {
        ElMessage.error(data.msg || '新增失败')
    }
}

onMounted(() => getList())
</script>

<style scoped>
.template-page {
    padding: 20px;
}
.toolbar {
    margin-bottom: 20px;
}
.template-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}
.template-card {
    width: 260px;
    position: relative;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
}
.template-card:hover {
    transform: translateY(-3px);
}
.tpl-name {
    font-weight: 800;
    font-size: 18px;
    margin-bottom: 4px;
}
.tpl-subject {
    font-weight: 500;
    font-size: 14px;
    margin-bottom: 6px;
}
.tpl-content {
    font-size: 13px;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    line-clamp: 3;
    -webkit-box-orient: vertical;
    white-space: pre-line;
}
.delete-btn {
    position: absolute;
    top: 8px;
    right: 10px;
    font-size: 16px;
    transition: 0.2s;
}
.dialog-footer {
    text-align: right;
}
</style>
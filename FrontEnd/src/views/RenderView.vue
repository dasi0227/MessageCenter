<template>
    <div class="render-page">
        <!-- 使用说明 -->
        <el-card class="guide-card" shadow="never">
            <p><strong>使用说明：</strong></p>
            <ul>
                <li>使用 <strong>${name}</strong> 包裹变量名，即可自动渲染为对应的 value。</li>
                <li>系统默认变量以 <strong>#</strong> 开头：
                    <strong>#contact</strong>（联系人名称），
                    <strong>#department</strong>（部门名称），
                    <strong>#account</strong>（操作人名称），
                    <strong>#date</strong>（日期），
                    <strong>#datetime</strong>（时间）。
                </li>
                <li><strong>不允许嵌套渲染</strong>，即 name 内部不能有 ${} 结构。</li>
            </ul>
        </el-card>

        <!-- 操作栏 -->
        <div class="toolbar">
            <el-button type="primary" @click="handleAdd">新增变量</el-button>
        </div>

        <!-- 卡片区 -->
        <div class="render-grid">
            <el-card
                v-for="item in renderList"
                :key="item.id"
                class="render-card"
                :class="{ system: item.name.startsWith('#') }"
                shadow="hover"
                @click="openEdit(item)"
            >
                <div class="render-key">{{ item.name }}</div>
                <div class="render-value">{{ item.value ?? 'null' }}</div>
                <div v-if="item.remark" class="render-remark">{{ item.remark }}</div>

                <el-icon
                    v-if="!item.name.startsWith('#')"
                    class="delete-btn"
                    @click.stop="handleDelete(item)"
                >
                    <Close />
                </el-icon>
            </el-card>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增变量" width="500px" align-center>
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="名称">
                    <el-input v-model="addForm.name" placeholder="请输入变量名，例如 greeting" />
                </el-form-item>
                <el-form-item label="值">
                    <el-input v-model="addForm.value" placeholder="请输入变量值" />
                </el-form-item>
                <el-form-item label="备注">
                    <el-input
                        type="textarea"
                        v-model="addForm.remark"
                        :rows="3"
                        placeholder="可选，说明用途"
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

        <!-- 编辑弹窗 -->
        <el-dialog v-model="editVisible" title="查看变量" width="500px" align-center>
            <el-form :model="editForm" label-width="80px">
                <el-form-item label="名称">
                    <el-input
                        v-model="editForm.name"
                        :disabled="isSystemName(editForm.name)"
                    />
                </el-form-item>
                <el-form-item label="值">
                    <el-input
                        v-model="editForm.value"
                        :disabled="isSystemName(editForm.name)"
                    />
                </el-form-item>
                <el-form-item label="备注">
                    <el-input
                        type="textarea"
                        v-model="editForm.remark"
                        :rows="3"
                        :disabled="isSystemName(editForm.name)"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="editVisible = false">关闭</el-button>
                    <el-button
                        v-if="!isSystemName(editForm.name)"
                        type="primary"
                        @click="submitEdit"
                    >
                        保存修改
                    </el-button>
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

const renderList = ref([])
const addVisible = ref(false)
const editVisible = ref(false)

const addForm = ref({ name: '', value: '', remark: '' })
const editForm = ref({ id: null, name: '', value: '', remark: '' })

const isSystemName = (name) => name?.startsWith('#')

// 获取列表
const getList = async () => {
    const { data } = await request.get('/render/list')
    if (data.code === 200) renderList.value = data.data
    else ElMessage.error(data.msg || '加载失败')
}

// 打开编辑弹窗
const openEdit = (item) => {
    editForm.value = { ...item }
    editVisible.value = true
}

// 提交修改
const submitEdit = async () => {
    if (!editForm.value.name || !editForm.value.value)
        return ElMessage.warning('名称和值不能为空')

    const { data } = await request.post('/render/update', editForm.value)
    if (data.code === 200) {
        ElMessage.success('修改成功')
        editVisible.value = false
        getList()
    } else {
        ElMessage.error(data.msg || '修改失败')
    }
}

// 删除
const handleDelete = (item) => {
    ElMessageBox.confirm(`确定要删除变量「${item.name}」吗？`, '提示', { type: 'warning' })
        .then(async () => {
            const { data } = await request.post(`/render/remove/${item.id}`)
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
    addForm.value = { name: '', value: '', remark: '' }
    addVisible.value = true
}

// 提交新增
const submitAdd = async () => {
    if (!addForm.value.name || !addForm.value.value)
        return ElMessage.warning('名称和值不能为空')

    const { data } = await request.post('/render/add', addForm.value)
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
.render-page {
    padding: 20px;
}
.guide-card {
    border-left: 4px solid #409eff;
    margin-bottom: 20px;
    padding: 5px;
}
.guide-card li {
    margin-bottom: 10px;
    line-height: 1.6;
}
.guide-card li:last-child {
    margin-bottom: 0;
}
.toolbar {
    margin-bottom: 20px;
}
.render-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}
.render-card {
    width: 260px;
    position: relative;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
}
.render-card:hover {
    transform: translateY(-3px);
}
.render-card.system {
    border: 2px solid maroon;
    cursor: default;
}
.render-key {
    font-weight: 600;
    font-size: 18px;
    margin-bottom: 4px;
}
.render-value {
    font-size: 17px;
    word-break: break-all;
}
.render-remark {
    font-size: 13px;
    color: #999;
    font-style: italic;
    margin-top: 12px;
}
.delete-btn {
    position: absolute;
    top: 8px;
    right: 10px;
    font-size: 16px;
    transition: 0.2s;
}
.delete-btn:hover {
    color: #f56c6c;
}
.dialog-footer {
    text-align: right;
}
</style>
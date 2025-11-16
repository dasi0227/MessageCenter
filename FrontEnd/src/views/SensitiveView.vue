<template>
    <div class="sensitive-page">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
            <el-button type="primary" @click="handleAdd">新增敏感词</el-button>
        </div>

        <!-- 敏感词卡片区 -->
        <div class="word-grid">
            <el-card
                v-for="word in wordList"
                :key="word.id"
                class="word-card"
                :class="{ editing: editId === word.id }"
                shadow="hover"
            >
                <div
                    class="word-text"
                    :contenteditable="editId === word.id"
                    :class="{ editing: editId === word.id }"
                    @click="startEdit(word)"
                    @blur="submitEdit(word, $event)"
                    @keydown.enter.prevent="onEnter($event)"
                >
                    {{ word.word }}
                </div>
                <el-icon class="delete-btn" @click.stop="handleDelete(word)">
                    <Close />
                </el-icon>
            </el-card>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog v-model="addVisible" title="新增敏感词" width="400px" align-center>
            <div class="add-dialog">
                <div v-for="(input, index) in addInputs" :key="index" class="add-input-row">
                    <el-input
                        v-model="addInputs[index]"
                        placeholder="输入敏感词..."
                        @input="handleDynamicInput(index)"
                        @keyup.enter="handleDynamicInput(index)"
                    />
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="addVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitAdd">提交</el-button>
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

const wordList = ref([])
const editId = ref(null)
const addVisible = ref(false)
const addInputs = ref([''])
const isSubmitting = ref(false)

// 获取列表
const getList = async () => {
    const { data } = await request.get('/sensitive/list')
    if (data.code === 200) wordList.value = data.data
    else ElMessage.error(data.msg || '加载失败')
}

// 删除
const handleDelete = (word) => {
    ElMessageBox.confirm(`确定要删除敏感词「${word.word}」吗？`, '提示', {
        type: 'warning'
    })
        .then(async () => {
            const { data } = await request.post(`/sensitive/remove/${word.id}`)
            if (data.code === 200) {
                ElMessage.success('删除成功')
                getList()
            } else {
                ElMessage.error(data.msg || '删除失败')
            }
        })
        .catch(() => {})
}

// 开始编辑
const startEdit = (word) => {
    editId.value = word.id
}

// 按 Enter 时触发 blur 提交
const onEnter = (e) => {
    e.preventDefault()
    e.target.blur()
}

// 提交编辑
const submitEdit = async (word, event) => {
    if (isSubmitting.value) return
    isSubmitting.value = true

    const newWord = event.target.innerText.trim()
    if (!newWord) {
        ElMessage.warning('敏感词不能为空')
        event.target.innerText = word.word
        editId.value = null
        isSubmitting.value = false
        return
    }
    if (newWord === word.word) {
        editId.value = null
        isSubmitting.value = false
        return
    }

    try {
        const { data } = await request.post('/sensitive/update', { id: word.id, word: newWord })
        if (data.code === 200) {
            ElMessage.success('修改成功')
            editId.value = null
            getList()
        } else {
            ElMessage.error(data.msg || '修改失败')
            event.target.innerText = word.word
            editId.value = null
        }
    } catch {
        ElMessage.error('请求异常，修改未保存')
        event.target.innerText = word.word
        editId.value = null
    } finally {
        isSubmitting.value = false
    }
}

// 打开新增弹窗
const handleAdd = () => {
    addVisible.value = true
    addInputs.value = ['']
}

// 输入逻辑：保持前面无空行 + 始终多一个空输入框
const handleDynamicInput = (index) => {
    // 去掉中间空行
    addInputs.value = addInputs.value.filter((w, i, arr) => w.trim() !== '' || i === arr.length - 1)
    const last = addInputs.value[addInputs.value.length - 1]
    if (last.trim() !== '') addInputs.value.push('')
}

// 提交新增
const submitAdd = async () => {
    const words = addInputs.value.map((w) => w.trim()).filter((w) => w)
    if (!words.length) return ElMessage.warning('请输入至少一个敏感词')

    const { data } = await request.post('/sensitive/add', { words })
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
.sensitive-page {
    padding: 20px;
}
.toolbar {
    margin-bottom: 20px;
}
.word-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}
.word-card {
    position: relative;
    width: 200px;
    cursor: text;
    text-align: center;
    border-radius: 8px;
    transition: all 0.2s;
}
.word-card:hover {
    transform: translateY(-3px);
}
.word-card.editing {
    border: 2px solid maroon;
}
.word-text {
    font-size: 18px;
    font-weight: 500;
    min-height: 32px;
    outline: none;
}
.delete-btn {
    position: absolute;
    top: 6px;
    right: 8px;
    font-size: 16px;
    cursor: pointer;
    transition: 0.2s;
}
/* 弹窗样式 */
.add-dialog {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.add-input-row {
    width: 100%;
}
.dialog-footer {
    text-align: right;
}
</style>
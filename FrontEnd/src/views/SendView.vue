<template>
    <div class="send-page">
        <el-card shadow="never" class="send-card">
            <!-- 部门 -->
            <div class="form-row">
                <span class="label">部门：</span>
                <div class="department-container">
                    <el-button @click="openDepartmentDialog" type="primary" plain>选择部门</el-button>
                    <div class="value-list">
                        <el-tag v-if="selectedDepartment" closable type="info" @close="removeDepartment">
                            {{ selectedDepartment.name }}
                        </el-tag>
                    </div>
                </div>
            </div>

            <!-- 联系人 -->
            <div class="form-row">
                <span class="label">联系人：</span>
                <el-button @click="openContactDialog" type="primary" plain>选择联系人</el-button>
                <div class="value-list">
                    <el-tag v-for="c in selectedContacts" :key="c.id" closable @close="removeContact(c.id)" type="info">
                        {{ c.name }}
                    </el-tag>
                </div>
            </div>

            <!-- 渠道 -->
            <div class="form-row">
                <span class="label">渠道：</span>
                <div class="channel-options">
                    <el-check-tag
                        v-for="c in channelList"
                        :key="c"
                        :checked="selectedChannel === c"
                        @change="() => selectedChannel = c"
                    >
                        {{ c }}
                    </el-check-tag>
                </div>
            </div>

            <!-- 附件 -->
            <div class="form-row">
                <span class="label">附件：</span>
                <div class="attachment-container">
                    <el-button type="primary" plain @click="triggerFileSelect">选择文件</el-button>
                    <input ref="fileInputRef" type="file" multiple style="display:none" @change="handleFileChange" />
                    <div class="file-list">
                        <el-tag
                            v-for="(f, i) in attachments"
                            :key="f.name+i"
                            closable
                            type="success"
                            @close="removeFile(i)"
                        >
                            {{ f.name }}
                        </el-tag>
                    </div>
                </div>
            </div>

            <!-- 模板 -->
            <div class="form-row">
                <span class="label">模板：</span>
                <div class="template-container">
                    <el-button @click="openTemplateDialog" type="primary" plain>选择模板</el-button>
                    <div class="value-list">
                        <el-tag
                            v-if="selectedTemplate"
                            closable
                            type="info"
                            @close="removeTemplate"
                        >
                            {{ selectedTemplate.name }}
                        </el-tag>
                    </div>
                </div>
            </div>

            <!-- 定时发送 -->
            <div class="form-row">
                <span class="label">定时：</span>
                <div class="schedule-container">
                    <el-button type="primary" plain @click="openScheduleDialog">选择时间</el-button>
                    <div class="value-list">
                        <el-tag
                            v-if="scheduledTime"
                            closable
                            type="warning"
                            @close="removeSchedule"
                        >
                            {{ formatSchedule(scheduledTime) }}
                        </el-tag>
                    </div>
                </div>
            </div>

            <!-- 标题 -->
            <div class="form-row">
                <span class="label">标题：</span>
                <el-input v-model="subject" placeholder="请输入消息标题" style="width:400px" />
            </div>

            <!-- 内容 -->
            <div class="form-row">
                <span class="label">内容：</span>
                <el-input
                    type="textarea"
                    v-model="content"
                    :rows="8"
                    placeholder="请输入消息内容"
                    style="width:600px"
                />
            </div>

            <div class="form-row" style="margin-top:20px">
                <el-button type="primary" @click="handleSend">发送</el-button>
                <el-button @click="resetForm">重置</el-button>
            </div>
        </el-card>

        <!-- 部门弹窗 -->
        <el-dialog v-model="departmentDialogVisible" title="选择部门" width="500px" :close-on-click-modal="false">
            <div class="dialog-header-bar">
                <el-input v-model="departmentSearch" placeholder="输入部门名称查找" clearable style="width:60%" />
                <div class="dialog-buttons">
                    <el-button @click="departmentDialogVisible=false">取消</el-button>
                    <el-button type="primary" @click="confirmDepartment">确定</el-button>
                </div>
            </div>
            <div class="dialog-table-wrapper">
                <el-table :data="filteredDepartments" @row-click="selectDepartment" highlight-current-row :row-class-name="highlightRow" height="340">
                    <el-table-column prop="name" label="部门名称" />
                </el-table>
            </div>
        </el-dialog>

        <!-- 联系人弹窗 -->
        <el-dialog
            v-model="contactDialogVisible"
            title="选择联系人"
            width="800px"
            :close-on-click-modal="false"
            @opened="reselectVisibleRows"
        >
            <div class="dialog-header-bar">
                <el-input v-model="contactSearch" placeholder="输入联系人名称查找" clearable style="width:60%" />
                <div class="dialog-buttons">
                    <el-button @click="contactDialogVisible=false">取消</el-button>
                    <el-button type="primary" @click="confirmContacts">确定</el-button>
                </div>
            </div>
            <div class="dialog-table-wrapper">
                <el-table
                    ref="contactTableRef"
                    :data="filteredContacts"
                    row-key="id"
                    :reserve-selection="true"
                    @selection-change="contactSelectionChange"
                    :row-class-name="highlightRow"
                    height="340"
                >
                    <el-table-column type="selection" width="55" />
                    <el-table-column prop="name" label="联系人姓名" width="120" />
                    <el-table-column prop="phone" label="手机号" width="140">
                        <template #default="{ row }">{{ row.phone || '-' }}</template>
                    </el-table-column>
                    <el-table-column prop="email" label="邮箱" min-width="220">
                        <template #default="{ row }">{{ row.email || '-' }}</template>
                    </el-table-column>
                    <el-table-column prop="inbox" label="信箱" min-width="180">
                        <template #default="{ row }">{{ row.inbox || '-' }}</template>
                    </el-table-column>
                </el-table>
            </div>
        </el-dialog>

        <!-- 模板弹窗 -->
        <el-dialog v-model="templateDialogVisible" title="选择模板" width="600px" :close-on-click-modal="false">
            <div class="dialog-header-bar">
                <el-input v-model="templateSearch" placeholder="输入模板名称查找" clearable style="width:60%" />
                <div class="dialog-buttons">
                    <el-button @click="templateDialogVisible=false">取消</el-button>
                    <el-button type="primary" @click="confirmTemplate">确定</el-button>
                </div>
            </div>
            <div class="dialog-table-wrapper">
                <el-table :data="filteredTemplates" highlight-current-row @row-click="selectTemplate" :row-class-name="highlightRow" height="340">
                    <el-table-column prop="name" label="模板名称" />
                </el-table>
            </div>
        </el-dialog>

        <!-- 定时弹窗 -->
        <el-dialog v-model="scheduleDialogVisible" title="选择发送时间" width="400px" :close-on-click-modal="false">
            <div style="text-align:center; padding: 20px;">
                <el-date-picker
                    v-model="tempScheduledTime"
                    type="datetime"
                    placeholder="选择日期时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                />
            </div>
            <template #footer>
                <el-button @click="scheduleDialogVisible=false">取消</el-button>
                <el-button type="primary" @click="confirmSchedule">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import request from '../api/request'
import { ElMessage } from 'element-plus'

// ---------- 基础状态 ----------
const selectedDepartment = ref(null)
const selectedContacts = ref([])
const selectedChannel = ref('')
const attachments = ref([])
const selectedTemplate = ref(null)
const subject = ref('')
const content = ref('')
const scheduledTime = ref(null)

// ---------- 弹窗控制 ----------
const departmentDialogVisible = ref(false)
const contactDialogVisible = ref(false)
const templateDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)

// ---------- 数据源 ----------
const departmentList = ref([])
const contactList = ref([])
const templateList = ref([])
const channelList = ref([])

// ---------- 搜索 ----------
const departmentSearch = ref('')
const contactSearch = ref('')
const templateSearch = ref('')

// ---------- 过滤（缺失这三处会导致整页白屏） ----------
const filteredDepartments = computed(() =>
    departmentList.value.filter(d =>
        d?.name?.toLowerCase?.().includes(departmentSearch.value.toLowerCase())
    )
)
const filteredContacts = computed(() =>
    contactList.value.filter(c =>
        c?.name?.toLowerCase?.().includes(contactSearch.value.toLowerCase())
    )
)
const filteredTemplates = computed(() =>
    templateList.value.filter(t =>
        t?.name?.toLowerCase?.().includes(templateSearch.value.toLowerCase())
    )
)

// ---------- 初始化 ----------
onMounted(async () => {
    const [depRes, contactRes, tempRes, channelRes] = await Promise.all([
        request.get('/department/list'),
        request.get('/contact/list'),
        request.get('/template/list'),
        request.get('/message/channel/list'),
    ])
    if (depRes.data.code === 200) departmentList.value = depRes.data.data
    if (contactRes.data.code === 200) contactList.value = contactRes.data.data
    if (tempRes.data.code === 200) templateList.value = tempRes.data.data
    if (channelRes.data.code === 200) channelList.value = channelRes.data.data
})

// ---------- 部门 ----------
const openDepartmentDialog = () => (departmentDialogVisible.value = true)
let tempSelectedDepartment = null
const selectDepartment = (row) => (tempSelectedDepartment = row)
const confirmDepartment = () => {
    if (tempSelectedDepartment) selectedDepartment.value = tempSelectedDepartment
    departmentDialogVisible.value = false
}
const removeDepartment = () => (selectedDepartment.value = null)

// ---------- 联系人 ----------
const contactTableRef = ref()
const tempSelectedContactIds = ref(new Set())
const suppressSelectionChange = ref(false)
const reselectVisibleRows = async () => {
    await nextTick()
    const table = contactTableRef.value
    if (!table) return
    suppressSelectionChange.value = true
    table.clearSelection()
    filteredContacts.value.forEach(row => {
        if (tempSelectedContactIds.value.has(row.id)) {
            table.toggleRowSelection(row, true)
        }
    })
    setTimeout(() => (suppressSelectionChange.value = false), 50)
}
const openContactDialog = () => {
    tempSelectedContactIds.value = new Set(selectedContacts.value.map(c => c.id))
    contactDialogVisible.value = true
}
watch(filteredContacts, () => {
    if (contactDialogVisible.value) reselectVisibleRows()
})
const contactSelectionChange = (visibleSelectedRows) => {
    if (suppressSelectionChange.value) return
    const visibleIds = new Set(filteredContacts.value.map(r => r.id))
    const selectedIdsInView = new Set(visibleSelectedRows.map(r => r.id))
    visibleIds.forEach(id => {
        if (selectedIdsInView.has(id)) tempSelectedContactIds.value.add(id)
        else tempSelectedContactIds.value.delete(id)
    })
}
const confirmContacts = () => {
    const ids = tempSelectedContactIds.value
    selectedContacts.value = contactList.value.filter(c => ids.has(c.id))
    contactDialogVisible.value = false
}
const removeContact = (id) => {
    selectedContacts.value = selectedContacts.value.filter(c => c.id !== id)
    tempSelectedContactIds.value.delete(id)
}

// ---------- 模板 ----------
const openTemplateDialog = () => (templateDialogVisible.value = true)
let tempSelectedTemplate = null
const selectTemplate = (row) => (tempSelectedTemplate = row)
const confirmTemplate = () => {
    if (tempSelectedTemplate) {
        selectedTemplate.value = tempSelectedTemplate
        subject.value = tempSelectedTemplate.subject
        content.value = tempSelectedTemplate.content
    }
    templateDialogVisible.value = false
}
const removeTemplate = () => {
    selectedTemplate.value = null
    subject.value = ''
    content.value = ''
}

// ---------- 定时 ----------
const tempScheduledTime = ref(null)
const openScheduleDialog = () => {
    tempScheduledTime.value = scheduledTime.value
    scheduleDialogVisible.value = true
}
const confirmSchedule = () => {
    scheduledTime.value = tempScheduledTime.value
    scheduleDialogVisible.value = false
}
const removeSchedule = () => (scheduledTime.value = null)
const formatSchedule = (time) => (time ? time : '')

// ---------- 附件 ----------
const fileInputRef = ref(null)
const triggerFileSelect = () => fileInputRef.value?.click()
const handleFileChange = (e) => {
    const files = Array.from(e.target.files || [])
    attachments.value = attachments.value.concat(files.map(f => ({ name: f.name })))
    e.target.value = ''
}
const removeFile = (i) => attachments.value.splice(i, 1)

// ---------- 行高亮 ----------
const highlightRow = ({ row }) => {
    if (
        row === tempSelectedDepartment ||
        row === tempSelectedTemplate ||
        tempSelectedContactIds.value.has?.(row.id)
    ) return 'selected-row'
    return ''
}

// ---------- 发送 ----------
const handleSend = async () => {
    if (!selectedDepartment.value || !selectedContacts.value.length || !selectedChannel.value || !subject.value || !content.value) {
        const missing = []
        if (!selectedDepartment.value) missing.push('部门')
        if (!selectedContacts.value.length) missing.push('联系人')
        if (!selectedChannel.value) missing.push('渠道')
        if (!subject.value) missing.push('标题')
        if (!content.value) missing.push('内容')
        ElMessage.warning(`请填写以下必填项：${missing.join('、')}`)
        return
    }
    try {
        const sanitize = (str) => (str ? str.replace(/\\\$/g, '$') : str)
        const payload = {
            templateId: selectedTemplate.value?.id || null,
            channel: selectedChannel.value,
            subject: sanitize(subject.value),
            content: sanitize(content.value),
            attachments: attachments.value.map(f => f.name),
            departmentId: selectedDepartment.value.id,
            departmentName: selectedDepartment.value.name,
            contactIds: selectedContacts.value.map(c => c.id),
            scheduledTime: scheduledTime.value || null
        }
        const { data } = await request.post('/message/send', payload)
        if (data.code === 200) {
            ElMessage.success('发送成功')
            resetForm()
        } else {
            ElMessage.error(data.msg || '发送失败')
        }
    } catch {
        ElMessage.error('请求失败')
    }
}

// ---------- 重置 ----------
const resetForm = () => {
    selectedDepartment.value = null
    selectedContacts.value = []
    tempSelectedContactIds.value = new Set()
    selectedChannel.value = ''
    attachments.value = []
    selectedTemplate.value = null
    subject.value = ''
    content.value = ''
    scheduledTime.value = null
}
</script>

<style scoped>
.send-page{padding:20px;}
.send-card{padding:20px;}
.form-row{display:flex;align-items:center;margin-bottom:18px;}
.label{width:90px;font-weight:600;}
.value-list{margin-left:10px;display:flex;flex-wrap:wrap;gap:8px;}
.department-container,.template-container,.attachment-container,.schedule-container{display:flex;align-items:center;gap:10px;}
.file-list{display:flex;flex-wrap:wrap;gap:8px;align-items:center;}
.channel-options{display:flex;gap:10px;margin-left:10px;}
.dialog-header-bar{display:flex;align-items:center;justify-content:space-between;margin-bottom:10px;}
.dialog-buttons{display:flex;gap:8px;}
.dialog-table-wrapper{height:360px;overflow-y:auto;}
.selected-row{background-color:#d9ecff!important;}
</style>
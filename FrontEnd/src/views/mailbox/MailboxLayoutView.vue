<template>
    <div class="mailbox-layout">

        <!-- 顶部导航 -->
        <el-header class="header">
            <div class="left-group">
                <div class="left">
                    <strong>📮 Dasi Mailbox</strong>
                </div>
                <div class="nav">
                    <el-button link @click="$router.push('/mailbox/reserve')" :type="active === 'reserve' ? 'primary' : 'default'">✉️ 收件箱</el-button>
                    <el-button link @click="$router.push('/mailbox/recycle')" :type="active === 'recycle' ? 'primary' : 'default'">🗑️ 回收站</el-button>
                </div>
            </div>

            <div class="right">
                <el-dropdown>
                    <span class="contact-info">
                        🙎 {{ store.name }}（{{ store.inbox }}）
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item disabled>📱 {{ store.phone }}</el-dropdown-item>
                            <el-dropdown-item disabled>📧 {{ store.email }}</el-dropdown-item>
                            <el-dropdown-item divided @click="openEdit">修改信息</el-dropdown-item>
                            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <!-- 主体 -->
        <el-main class="main">
            <router-view />
        </el-main>

        <!-- 底部信息栏 -->
        <el-footer class="footer">
            <div class="footer-content">
                <span>© 2025 Dasi · Mailbox · V3.0</span>
                <div class="links">
                    <a href="https://dasi.plus" target="_blank">
                        <el-icon><Link /></el-icon>
                        博客
                    </a>
                    <span>|</span>
                    <a href="https://github.com/dasi0227/MessageCenter" target="_blank">
                        <el-icon><StarFilled /></el-icon>
                        GitHub
                    </a>
                </div>
            </div>
        </el-footer>

        <el-dialog v-model="dialogVisible" title="修改信息" width="450px">
            <el-form :model="form" label-width="80px">

                <el-form-item label="姓名">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="form.password" show-password placeholder="不修改可留空" />
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="form.email" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitEdit">保存</el-button>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useContactStore } from '../../store/contact'
import { Link, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../api/request'

const store = useContactStore()
const route = useRoute()
const router = useRouter()


const logout = () => {
    store.clearContact()
    router.push('/mailbox/login')
}

const active = computed(() =>
    route.path.includes('recycle') ? 'recycle' : 'reserve'
)

// ======================
// 自动刷新邮箱 Token
// ======================
let refreshTimer = null
const refreshMailbox = async () => {
    const { data } = await request.post('/mailbox/refresh')
    if (data.code === 200 && data.data) {
        store.token = data.data
        localStorage.setItem(
            'contact_state',
            JSON.stringify(store.$state)
        )
        request.defaults.headers['Authorization-Mailbox'] = data.data
    }
}
onMounted(() => {
    refreshTimer = setInterval(refreshMailbox, 30000)
    refreshMailbox()
})
onBeforeUnmount(() => {
    if (refreshTimer) clearInterval(refreshTimer)
})

/* ======================
   修改信息弹窗逻辑
====================== */

const dialogVisible = ref(false)

const form = reactive({
    name: '',
    password: '',
    phone: '',
    email: ''
})

const openEdit = () => {
    form.name = store.name
    form.password = ''
    form.phone = store.phone
    form.email = store.email
    dialogVisible.value = true
    console.log("store.name =", store.name)
    console.log("form.name =", form.name)
}

const submitEdit = async () => {
    const payload = {
        name: form.name,
        password: form.password || '',  // 留空表示不修改
        phone: form.phone,
        email: form.email
    }

    const { data } = await request.post('/mailbox/update', payload)

    if (data.code === 200) {
        store.name = payload.name
        store.phone = payload.phone
        store.email = payload.email

        ElMessage.success('修改成功')
        dialogVisible.value = false
    }
}
</script>

<style scoped>
/* 总容器 */
.mailbox-layout {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f8f9fb;
}

/* 顶部导航 */
.header {
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 25px;
    border-bottom: 1px solid #dcdfe6;
    background: white;
    font-weight: 700;
}

/* left + nav 放在同一个 flex 容器 */
.left-group {
    display: flex;
    align-items: center;
    gap: 20px;
}

.left {
    font-size: 40px !important;
    font-weight: 700;
}

.nav {
    display: flex;
    align-items: center;
    margin-top: 7px;
}

/* 字体大小统一调大 */
.header .el-button  {
    font-size: 30px !important;
    font-weight: 700;
}
.header .contact-info {
    font-size: 20px !important;
}

.right {
    display: flex;
    align-items: center;
}

/* 右侧 dropdown */
.contact-info {
    cursor: pointer;
    color: #409eff;
}
:deep(.el-dropdown *:focus) {
    outline: none !important;
    box-shadow: none !important;
}

/* 主体区域 */
.main {
    flex: 1;
    padding: 20px;
}

/* 底部 Footer */
.footer {
    height: 60px;
    background: white;
    border-top: 1px solid #e4e7ed;
    display: flex;
    align-items: center;
    justify-content: center;
}
.footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 90%;
    font-size: 14px;
    color: #666;
}
.links {
    display: flex;
    align-items: center;
    gap: 12px;
}
.links a {
    display: flex;
    align-items: center;
    gap: 5px;
    color: #3311e0;
    text-decoration: none;
    transition: color 0.2s, transform 0.2s ease;
    font-weight: 500;
}

.links a:hover {
    color: #000;
    transform: scale(1.12);
}
</style>
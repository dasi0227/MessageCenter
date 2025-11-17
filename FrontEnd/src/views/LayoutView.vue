<template>
    <el-container class="layout-container">
        <!-- 左侧菜单栏 -->
        <el-aside :width="isCollapsed ? '32px' : '220px'" class="aside">
            <div class="aside-header" v-if="!isCollapsed">
                <img class="avatar" src="../assets/images/logo.webp" alt="avatar" />
                <div class="account-info">
                    <div class="name">{{ name }}</div>
                    <div class="role" :class="{ admin: role === 'ADMIN', user: role !== 'ADMIN' }">
                        {{ role }}
                    </div>
                </div>
            </div>

            <el-menu
                :default-active="activeMenu"
                router
                background-color="#1e2b3a"
                text-color="#bfcbd9"
                active-text-color="#FF6B6B"
                :collapse="isCollapsed"
            >
                <!-- 系统模块 -->
                <el-sub-menu index="1">
                    <template #title>
                        <i class="el-icon-setting"></i>
                        <span>系统模块</span>
                    </template>
                    <el-menu-item index="/dashboard">仪表盘</el-menu-item>
                    <el-menu-item index="/account">账户管理</el-menu-item>
                    <el-menu-item index="/department">部门管理</el-menu-item>
                    <el-menu-item index="/contact">联系人管理</el-menu-item>
                </el-sub-menu>

                <!-- 消息模块 -->
                <el-sub-menu index="2">
                    <template #title>
                        <i class="el-icon-s-tools"></i>
                        <span>消息模块</span>
                    </template>
                    <el-menu-item index="/send">发送消息</el-menu-item>
                    <el-menu-item index="/message">消息管理</el-menu-item>
                    <el-menu-item index="/template">模版管理</el-menu-item>
                    <el-menu-item index="/sensitive">敏感词管理</el-menu-item>
                    <el-menu-item index="/render">占位符管理</el-menu-item>
                </el-sub-menu>
            </el-menu>
        </el-aside>

        <!-- 右侧主区域 -->
        <el-container>
            <!-- 顶部导航栏 -->
            <el-header class="layout-header" :class="{ dark: darkMode }">
            <div class="header-left">
                <span class="brand">📮 Dasi MessageCenter {{ pageTitle }}</span>
            </div>

                <div class="header-right">
                    <div class="header-icons">
                    <!-- 打开站内信 -->
                        <el-tooltip content="打开站内信" placement="bottom">
                            <el-button link class="icon-btn" @click="openMailbox">
                                <el-icon size="22"><MessageBox /></el-icon>
                            </el-button>
                        </el-tooltip>
                        <!-- 查看错误消息 -->
                        <el-tooltip content="查看错误消息" placement="bottom">
                            <el-button link class="icon-btn" @click="goFailurePage">
                                <el-badge :value="unsolvedNum" :hidden="unsolvedNum === 0" type="danger">
                                    <el-icon size="22" :class="{ 'failure-active': isFailurePage }">
                                        <WarningFilled />
                                    </el-icon>
                                </el-badge>
                            </el-button>
                        </el-tooltip>
                    </div>
                    <!-- 侧边栏展开 -->
                    <el-tooltip :content="isCollapsed ? '收起菜单' : '展开菜单'" placement="bottom">
                        <el-switch
                            v-model="isCollapsed"
                            inline-prompt
                            active-text="展开"
                            inactive-text="收起"
                            class="collapse-switch"
                        >
                            <template #active-action>
                                <el-icon><Fold /></el-icon>
                            </template>
                            <template #inactive-action>
                                <el-icon><Expand /></el-icon>
                            </template>
                        </el-switch>
                    </el-tooltip>
                    <!-- 全屏切换 -->
                    <el-tooltip :content="isFullscreen ? '退出全屏' : '进入全屏'" placement="bottom">
                        <el-switch
                            v-model="isFullscreen"
                            @change="toggleFullscreen"
                            inline-prompt
                            active-text="全屏"
                            inactive-text="窗口"
                            class="fullscreen-switch"
                        >
                            <template #active-action>
                                <el-icon><FullScreen /></el-icon>
                            </template>
                            <template #inactive-action>
                                <el-icon><Monitor /></el-icon>
                            </template>
                        </el-switch>
                    </el-tooltip>

                    <!-- 明暗模式切换 -->
                    <el-tooltip :content="darkMode ? '切换亮色' : '切换暗色'" placement="bottom">
                        <el-switch
                            v-model="darkMode"
                            @change="toggleTheme"
                            inline-prompt
                            active-text="暗色"
                            inactive-text="亮色"
                            class="theme-switch"
                        >
                            <template #active-action>
                                <el-icon><Moon /></el-icon>
                            </template>
                            <template #inactive-action>
                                <el-icon><Sunny /></el-icon>
                            </template>
                        </el-switch>
                    </el-tooltip>

                    <!-- 刷新按钮 -->
                    <el-button type="success" plain class="refresh-btn" @click="refreshPage">
                        <el-icon><Refresh /></el-icon>
                        <span>刷新</span>
                    </el-button>

                    <!-- 退出按钮 -->
                    <el-button type="primary" plain class="logout-btn" @click="logout">
                        <el-icon><User /></el-icon>
                        <span>退出登录</span>
                    </el-button>
                </div>
            </el-header>

            <!-- 主内容区域 -->
            <el-main class="main" :class="{ dark: darkMode }">
                <router-view />
            </el-main>

            <!-- 底部信息栏 -->
            <el-footer class="layout-footer" :class="{ dark: darkMode }">
                <div class="footer-content">
                    <span>© 2025 Dasi · MessageCenter · V3.0</span>
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
        </el-container>
    </el-container>
</template>

<script setup>
/** ===================== 引入依赖 ===================== */
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Sunny, Moon, User, FullScreen, Monitor, Fold, Expand, Refresh, Link, StarFilled, WarningFilled, MessageBox } from '@element-plus/icons-vue'
import { useAccountStore } from '../store/account'
import request, { WEBSOCKET_BASE_URL } from '../api/request'

/** ===================== 基本状态与引用 ===================== */
const router = useRouter()
const account = useAccountStore()
const route = useRoute()
const pageTitle = ref(route.meta.title || '控制台')

const name = account.name
const role = account.role
const activeMenu = computed(() => {
    if (route.path.startsWith('/failure')) {
        return ''
    }
    return route.path
})
const darkMode = ref(false)
const isFullscreen = ref(false)
const isCollapsed = ref(false)

const isFailurePage = computed(() => route.path.startsWith('/failure'))

/** ===================== 定时器与数据状态 ===================== */
const unsolvedNum = ref(0) // 未解决数量
let ws = null              // WebSocket 实例
let timer = null           // 错误数量刷新定时器
let accountTimer = null    // 账号刷新定时器

/** ===================== WebSocket 告警功能 ===================== */
const connectWebSocket = () => {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
        console.log('⚠️ WebSocket 已存在，跳过重复连接')
        return
    }

    const wsUrl = `${WEBSOCKET_BASE_URL}/ws/notify`
    ws = new WebSocket(wsUrl)

    ws.onopen = () => console.log('✅ WebSocket 已连接')
    ws.onmessage = (event) => {
        try {
            const data = JSON.parse(event.data)
            ElMessage.error(data.message || '收到系统告警')
        } catch {
            ElMessage.error(event.data || '收到系统告警')
        }
    }
    ws.onclose = () => {
        console.warn('⚠️ WebSocket 断开，10 秒后重连...')
        ws = null
        setTimeout(connectWebSocket, 10000)
    }
    ws.onerror = () => ws.close()
}

/** ===================== 获取错误数量 ===================== */
const fetchFailureNum = async () => {
    try {
        const { data } = await request.get('/failure/num')
        if (data.code === 200) unsolvedNum.value = data.data || 0
    } catch {
        ElMessage.error('获取错误数量失败')
    }
}

/** ===================== 定时刷新账号信息 ===================== */
const refreshAccount = async () => {
    const { data } = await request.post('/account/refresh')
    if (data.code === 200 && data.data) {
        const newToken = data.data
        account.token = newToken
        localStorage.setItem('account_state', JSON.stringify(account.$state))
        request.defaults.headers['Authorization-Account'] = newToken
    }
}

/** ===================== 导航行为 ===================== */
const goFailurePage = () => router.push('/failure')
const openMailbox = () => {
    window.open('/mailbox/login', '_blank')
}

/** ===================== 生命周期钩子 ===================== */
onMounted(() => {
    connectWebSocket()
    fetchFailureNum()
    timer = setInterval(fetchFailureNum, 10000)         // 每 10 秒刷新错误数量
    accountTimer = setInterval(refreshAccount, 30000)   // 每 30 秒刷新账号信息
})

onBeforeUnmount(() => {
    if (timer) clearInterval(timer)
    if (accountTimer) clearInterval(accountTimer)
})

/** ===================== 标题与主题逻辑 ===================== */
watch(() => route.path, () => {
    pageTitle.value = route.meta.title || '控制台'
}, { immediate: true })

const toggleFullscreen = (val) => {
    if (val && !document.fullscreenElement) {
        document.documentElement.requestFullscreen()
        ElMessage.success('已进入全屏模式')
    } else if (!val && document.fullscreenElement) {
        document.exitFullscreen()
        ElMessage.success('已退出全屏模式')
    }
}

const toggleTheme = (val) => {
    const root = document.documentElement
    if (val) {
        root.classList.add('dark')
        ElMessage.success('已切换为暗色模式')
    } else {
        root.classList.remove('dark')
        ElMessage.success('已切换为亮色模式')
    }
    window.dispatchEvent(new Event('app-theme-change'))
}

/** ===================== 账号退出操作 ===================== */
const logout = () => {
    if (ws) {
        ws.close()
        ws = null
    }
    localStorage.removeItem('token')
    localStorage.removeItem('account')
    ElMessage.success('已退出登录')
    router.push('/login')
}

/** ===================== 刷新缓存逻辑 ===================== */
const flushCacheByRoute = async () => {
    const entity = route.path.split('/')[1] || ''
    try {
        await request.post(`/system/flush/${entity}`)
        ElMessage.success(`刷新成功`)
    } catch (e) {
        ElMessage.error('刷新失败')
        console.error(e)
    }
}

/** ===================== 页面刷新逻辑 ===================== */
const refreshPage = async () => {
    await flushCacheByRoute()
    router.replace(route.fullPath)
}
</script>

<style scoped>
/** ===================== 根容器布局 ===================== */
.layout-container {
    display: flex;
    height: 100vh;
    overflow: hidden;
}
/** ===================== 左侧菜单栏 ===================== */
.aside {
    display: flex;
    flex-direction: column;
    transition: width 0.3s ease;
    height: 100%;
}
.aside-header {
    display: flex;
    align-items: center;
    padding: 16px;
}
.avatar {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    margin-right: 15px;
}
.account-info .name {
    font-size: 18px;
    font-weight: 600;
    color: #fff;
}
.account-info .role {
    font-size: 12px;
    margin-top: 4px;
    font-weight: 500;
}
.account-info .role.admin {
    color: #ff4d4f;
}
.account-info .role.user {
    color: #52c41a;
}
.el-menu {
    border-right: none;
    transition: all 0.3s ease;
}

/** ===================== 主体容器布局 ===================== */
.layout-container > .el-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
}

/** ===================== 顶部导航栏 ===================== */
.layout-header {
    flex-shrink: 0;
    height: 60px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}
.header-left .brand {
    color: #ffd04b;
    font-size: 35px;
    font-weight: bold;
}
.header-right {
    display: flex;
    align-items: center;
}

/** ===================== 主内容区域 ===================== */
.main {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    box-sizing: border-box;
    transition: background-color 0.3s;
}

/** ===================== 底部信息栏 ===================== */
.layout-footer {
    flex-shrink: 0;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.footer-content {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 16px;
}
.footer-content .links {
    display: flex;
    align-items: center;
    gap: 10px;
}
.footer-content .links a {
    display: flex;
    align-items: center;
    gap: 5px;
    color: #ffd04b;
    text-decoration: none;
    transition: color 0.2s, transform 0.2s ease;
    font-weight: 500;
}

.footer-content .links a:hover {
    color: #fff;
    transform: scale(1.12);
}

/** ===================== 控件与交互按钮 ===================== */
.fullscreen-switch,
.collapse-switch,
.theme-switch {
    transform: scale(1.4);
    margin-right: 30px;
    --el-switch-on-color: #2563eb;
    --el-switch-off-color: #9ca3af;
} 
.header-icons {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-right: 25px;
    margin-top: 2.5px;
}
.icon-btn {
    transform: scale(1.3);
    height: 32px !important;
    width: 32px !important;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}
.icon-btn .el-icon {
    color: #fff;
}
.icon-btn:hover .el-icon {
    transform: scale(1.15);
    transition: all 0.2s ease;
}
.logout-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    height: 32px;
    font-weight: 500;
    transition: all 0.2s;
}
.logout-btn:hover {
    background-color: #0d05e6;
    border-color: black;
    color: #fff;
}
/* 背景颜色 */
.aside {
    background-color: #1e2b3a;
    color: #fff;
}
.el-sub-menu__title:hover,
.el-menu-item:hover {
    background-color: #0a74de !important;
}
:deep(.layout-header), :deep(.layout-footer) {
    background-color: #409eff !important;
    color: white !important;
    transition: background-color 0.3s;
}

:deep(.layout-header.dark), :deep(.layout-footer.dark) {
    background-color: #0e203a !important;
    color: #f0f0f0 !important;
    transition: background-color 0.3s;
}
.failure-active {
    color: #FF6B6B !important;
}
</style>
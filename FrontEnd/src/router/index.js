import { createRouter, createWebHistory } from 'vue-router'
import { useAccountStore } from '../store/account'
import Login from '../views/LoginView.vue'
import Register from '../views/RegisterView.vue'
import Layout from '../views/LayoutView.vue'
import Dashboard from '../views/DashboardView.vue'


// 路由器
const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', component: Login, meta: { title: '登录' } },
        { path: '/register', component: Register, meta: { title: '注册' } },
        {
            path: '/',
            component: Layout,
            children: [
                { path: '/dashboard', component: Dashboard, meta: { title: '仪表盘' } },
                // 后续模块示例：
                // { path: '/account', component: Account, meta: { title: '账户管理' } },
                // { path: '/contact', component: Contact, meta: { title: '联系人管理' } },
            ]
        }
    ]
})

// 全局前置守卫
router.beforeEach((to) => {
    const pub = ['/login', '/register']
    const account = useAccountStore()
    account.loadAccount()
    if (!pub.includes(to.path) && !account.isLoggedIn) {
        return '/login'
    }
    document.title = to.meta.title
        ? `MessageCenter ${to.meta.title}`
        : 'MessageCenter 控制台'
})

export default router
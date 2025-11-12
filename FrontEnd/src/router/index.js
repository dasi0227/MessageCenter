import { createRouter, createWebHistory } from 'vue-router'
import { useAccountStore } from '../store/account'

// 基础视图
import Login        from '../views/LoginView.vue'
import Register     from '../views/RegisterView.vue'
import Layout       from '../views/LayoutView.vue'

// 功能模块视图
import Dashboard    from '../views/DashboardView.vue'
import Account      from '../views/AccountView.vue'
import Department   from '../views/DepartmentView.vue'
import Contact      from '../views/ContactView.vue'
import Sensitive    from '../views/SensitiveView.vue'
import Template     from '../views/TemplateView.vue'
import Render       from '../views/RenderView.vue'
import Message      from '../views/MessageView.vue'
import Dispatch     from '../views/DispatchView.vue'
import Send         from '../views/SendView.vue'
import Failure      from '../views/FailureView.vue'


// 路由器配置
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
                { path: '/account', component: Account, meta: { title: '账户管理' } },
                { path: '/department', component: Department, meta: { title: '部门管理' } },
                { path: '/contact', component: Contact, meta: { title: '联系人管理' } },
                { path: '/send', component: Send, meta: { title: '发送消息' } },
                { path: '/message', component: Message, meta: { title: '消息管理' } },
                { path: '/dispatch/:messageId', component: Dispatch, meta: { title: '消息详情' } },
                { path: '/template', component: Template, meta: { title: '模板管理' } },
                { path: '/sensitive', component: Sensitive, meta: { title: '敏感词管理' } },
                { path: '/render', component: Render, meta: { title: '占位符管理' } },
                { path: '/failure', component: Failure, meta: { title: '错误管理' } },
            ]
        }
    ]
})

// 全局前置守卫
router.beforeEach((to) => {
    const publicPages = ['/login', '/register']
    const account = useAccountStore()
    account.loadAccount()

    // 未登录访问私有页 → 跳登录
    if (!publicPages.includes(to.path) && !account.isLoggedIn) {
        return '/login'
    }

    // 动态修改标题
    document.title = to.meta.title
        ? `MessageCenter ${to.meta.title}`
        : 'MessageCenter 控制台'
})

export default router
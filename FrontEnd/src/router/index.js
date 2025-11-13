import { createRouter, createWebHistory } from 'vue-router'
import { useAccountStore } from '../store/account'
import { useContactStore } from '../store/contact'

// 基础视图
import Login        from '../views/LoginView.vue'
import Register     from '../views/RegisterView.vue'
import Layout       from '../views/LayoutView.vue'

// 站内信视图
import MailboxLogin  from '../views/mailbox/MailboxLoginView.vue'
import MailboxLayout from '../views/mailbox/MailboxLayoutView.vue'
import Reserve       from '../views/mailbox/ReservePageView.vue'
import Recycle       from '../views/mailbox/RecyclePageView.vue'

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
        // ======================
        // C 端：联系人系统
        // ======================
        { path: '/mailbox/login', component: MailboxLogin, meta: { title: '联系人登录', mailbox: true } },
        {
            path: '/mailbox',
            component: MailboxLayout,
            meta: { mailbox: true },
            children: [
                { path: 'reserve', component: Reserve, meta: { title: '收件箱', mailbox: true } },
                { path: 'recycle', component: Recycle, meta: { title: '回收站', mailbox: true } }
            ]
        },

        // ======================
        // B 端：后台系统
        // ======================
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
    const account = useAccountStore()
    const contact = useContactStore()

    account.loadAccount()
    contact.loadContact()

    // ======================
    // C 端：联系人系统
    // ======================
    if (to.meta.mailbox) {
        if (!contact.isLoggedIn && to.path !== '/mailbox/login') {
            return '/mailbox/login'
        }
        document.title = `Mailbox ${to.meta.title || ''}`
        return
    }

    // ======================
    // B 端：后台系统
    // ======================
    const publicPages = ['/login', '/register']
    if (!publicPages.includes(to.path) && !account.isLoggedIn) {
        return '/login'
    }
    document.title = to.meta.title
        ? `MessageCenter ${to.meta.title}`
        : 'MessageCenter 控制台'
})

export default router
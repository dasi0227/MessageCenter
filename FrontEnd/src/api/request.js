import axios from 'axios'
import router from '../router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAccountStore } from '../store/account'
import { useContactStore } from '../store/contact'
import { normalize } from '../util/normalize'

export const API_BASE_URL = 'http://localhost:8080/api'
export const WEBSOCKET_BASE_URL = 'ws://localhost:8080'

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 8000
})

// ------------------------------
// 请求拦截器
// ------------------------------
request.interceptors.request.use((config) => {

    if (config.data && !(config.data instanceof FormData)) {
        config.data = normalize(config.data)
    }

    const isMailbox = config.url.startsWith('/mailbox')

    if (isMailbox) {
        const contact = useContactStore()
        if (!contact.token) contact.loadContact()
        if (contact.token) {
            config.headers['Authorization-Contact'] = contact.token
        }
    } else {
        const account = useAccountStore()
        if (!account.token) account.loadAccount()
        if (account.token) {
            config.headers['Authorization-Account'] = account.token
        }
    }

    return config
})


// ------------------------------
// 响应拦截器（全局统一错误提示）
// ------------------------------
request.interceptors.response.use(
    (res) => {
        if (res.data instanceof Blob) {
            return res;
        }
        
        const { code, msg } = res.data

        if (code === 401) {
            const isMailbox = res.config.url.startsWith('/mailbox')
            const type = isMailbox ? 'contact' : 'account'
            showLogoutDialog(type)
            return new Promise(() => {})
        }

        if (code !== 200) {
            ElMessage.error(msg || '请求失败')
        }
        return res
    },
    (error) => {
        if (error.response) {
            const status = error.response.status

            if (status === 401) {
                ElMessage.error('未登录或登录已过期')
            } else if (status === 403) {
                ElMessage.error('无权限访问')
            } else if (status === 500) {
                ElMessage.error('服务器内部错误')
            }
        } else {
            ElMessage.error('网络异常，请检查网络连接')
        }

        console.error(error)
        return new Promise(() => {})
    }
)

let logoutDialogVisible = {
    account: false,
    contact: false
}

// 倒计时缓存
let countdownTimer = null

// 弹窗组件（单例）
const showLogoutDialog = (type) => {
    if (logoutDialogVisible[type]) return
    logoutDialogVisible[type] = true

    let seconds = 3

    ElMessageBox({
        title: '登录已过期',
        message: `您的登录已过期，${seconds} 秒后将自动返回登录页`,
        showCancelButton: false,
        showConfirmButton: false,
        showClose: false,
        closeOnPressEscape: false,
        closeOnClickModal: false,
        distinguishCancelAndClose: true,
        beforeClose: () => {},
    })

    countdownTimer = setInterval(() => {
        seconds--
        document.querySelector('.el-message-box__message').innerText =
            `您的登录已过期，${seconds} 秒后将自动返回登录页`

        if (seconds <= 0) {
            clearInterval(countdownTimer)
            countdownTimer = null
            logoutDialogVisible[type] = false

            if (type === 'contact') {
                const store = useContactStore()
                store.clearContact()
                router.push('/mailbox/login')
            } else {
                const store = useAccountStore()
                store.clearAccount()
                router.push('/login')
            }

            ElMessageBox.close()
        }
    }, 1000)
}

export default request
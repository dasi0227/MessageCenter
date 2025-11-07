import axios from 'axios'
import { useAccountStore } from '../store/account'
import { normalize } from '../utils/normalize'

// 统一请求前缀
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 8000
})

// 请求拦截：处理空字符串
request.interceptors.request.use((config) => {
    if (config.data) {
        config.data = normalize(config.data)
    }
    return config
})

// 请求拦截：附带 Authorization-Account 头部
request.interceptors.request.use((config) => {
    const account = useAccountStore()
    if (!account.token) account.loadAccount()
    if (account.token) {
        config.headers['Authorization-Account'] = `${account.token}`
    }
    return config
})

export default request
import axios from 'axios'
import { useAccountStore } from '../store/account'

// 统一请求前缀
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 8000
})

// 请求拦截：附带 Authorization-Account
request.interceptors.request.use((config) => {
    const account = useAccountStore()
    if (!account.token) account.loadAccount()
    if (account.token) {
        config.headers['Authorization-Account'] = `${account.token}`
    }
    return config
})

export default request
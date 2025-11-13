import axios from 'axios'
import { useAccountStore } from '../store/account'
import { useContactStore } from '../store/contact'
import { normalize } from '../util/normalize'

export const API_BASE_URL = 'http://localhost:8080/api'
export const WEBSOCKET_BASE_URL = 'ws://localhost:8080'

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 8000
})

// 序列化
request.interceptors.request.use((config) => {
    if (config.data) config.data = normalize(config.data)

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

export default request
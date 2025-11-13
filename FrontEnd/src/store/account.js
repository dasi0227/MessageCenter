import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
    state: () => ({
        id: null,
        name: '',
        role: '',
        token: ''
    }),
    getters: {
        isLoggedIn: s => !!s.token
    },
    actions: {
        setAccount(vo) {
            this.$patch(vo)
            localStorage.setItem('account_state', JSON.stringify(this.$state))
        },
        loadAccount() {
            const raw = localStorage.getItem('account_state')
            if (raw) this.$patch(JSON.parse(raw))
        },
        clearAccount() {
            this.$reset()
            localStorage.removeItem('account_state')
        }
    }
})
import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
    state: () => ({
        id: null,
        name: '',
        role: '',
        createdAt: '',
        token: ''
    }),
    getters: {
        isLoggedIn: (s) => !!s.token
    },
    actions: {
        setAccount(vo) {
            this.id = vo.id
            this.name = vo.name
            this.role = vo.role
            this.createdAt = vo.createdAt
            this.token = vo.token
            localStorage.setItem('account_state', JSON.stringify(this.$state))
        },
        loadAccount() {
            const raw = localStorage.getItem('account_state')
            if (raw) {
                const saved = JSON.parse(raw)
                this.$patch(saved)
            }
        },
        clearAccount() {
            this.$reset()
            localStorage.removeItem('account_state')
        }
    }
})
import { defineStore } from 'pinia'

export const useContactStore = defineStore('contact', {
    state: () => ({
        name: '',
        inbox: null,
        phone: '',
        email: '',
        token: ''
    }),
    getters: {
        isLoggedIn: s => !!s.token
    },
    actions: {
        setContact(vo) {
            this.$patch(vo)
            localStorage.setItem('contact_state', JSON.stringify(this.$state))
        },
        loadContact() {
            const raw = localStorage.getItem('contact_state')
            if (raw) this.$patch(JSON.parse(raw))
        },
        clearContact() {
            this.$reset()
            localStorage.removeItem('contact_state')
        }
    }
})
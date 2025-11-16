import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

import "@/assets/css/theme.css"
import "@/assets/css/global.css"
import "element-plus/theme-chalk/dark/css-vars.css"
import 'element-plus/dist/index.css'

import ElementPlus from 'element-plus'

import MessagePlugin from './plugin/message'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.use(MessagePlugin)

app.mount('#app')
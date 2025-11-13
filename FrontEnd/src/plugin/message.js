import { ElMessage } from 'element-plus'

export default {
    install(app) {
        const duration = 1000

        const origin = ElMessage

        // 核心包装器
        const wrapper = (opts) => {
            if (typeof opts === 'string') {
                opts = { message: opts }
            }
            return origin({
                duration,
                ...opts
            })
        }

        // 覆盖所有类型的方法
        ;['success', 'error', 'warning', 'info'].forEach(type => {
            wrapper[type] = (msg) =>
                origin({
                    message: msg,
                    type,
                    duration
                })
        })

        // 注入全局属性
        app.config.globalProperties.$message = wrapper
        Object.assign(ElMessage, wrapper)
    }
}
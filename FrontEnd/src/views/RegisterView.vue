<template>
    <div class="register-container">
        <el-card class="register-card">
            <h2>Message Center</h2>
            <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
                <el-form-item label="账户名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入账户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item class="form-actions">
                    <el-button type="primary" @click="handleRegister">注册</el-button>
                    <el-button type="text" @click="$router.push('/login')">返回登录</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const form = ref({ name: '', password: '' })
const formRef = ref()
const rules = {
    name: [{ required: true, message: '账户名不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleRegister = () => {
    formRef.value.validate(async (valid) => {
        if (!valid) return
        const { data } = await request.post('/account/register', form.value)
        if (data.code === 200) {
            ElMessage.success('注册成功，请前往登陆')
        } else {
            ElMessage.error(data.msg || '操作失败')
        }
    })
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f7fa;
}

.register-card {
    width: 70%;
    max-width: 420px;
    text-align: center;
    padding: 10px 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
}

h2 {
    text-align: center;
    margin-bottom: 50px;
    font-size: 35px;
    font-weight: 600;
    color: #333;
}

:deep(.el-input__inner) {
    height: 42px;
    font-size: 15px;
}

:deep(.el-button) {
    height: 40px;
    font-size: 15px;
    padding: 0 24px;
}

:deep(.el-form-item__label) {
    font-size: 15px;
    line-height: 42px;
    color: #333;
}

:deep(.el-input__inner) {
    height: 42px;
    font-size: 15px;
    line-height: 42px;
}

.form-actions {
    margin-top: 35px;
}
</style>
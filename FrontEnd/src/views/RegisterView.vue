<template>
    <div class="auth-container">
        <!-- 背景弹幕 -->
        <div class="danmu-container"></div>

        <el-card class="auth-card">
            <h2 class="auth-title">MessageCenter</h2>

            <el-form class="big-form" :model="form" ref="formRef" :rules="rules" label-width="80px">
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

import { useDanmu } from '@/assets/js/danmu'
useDanmu()

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
        }
    })
}
</script>

<style src="@/assets/css/auth.css"></style>
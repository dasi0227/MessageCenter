<template>
    <div class="auth-container">

        <!-- 炫酷弹幕背景 -->
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
                    <el-button type="primary" @click="handleLogin">登录</el-button>
                    <el-button type="text" @click="$router.push('/register')">前往注册</el-button>
                    <el-button type="text" @click="goMailbox">前往站内信</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAccountStore } from '../store/account'
import request from '../api/request'

import { useDanmu } from '@/assets/js/danmu'
useDanmu()

// ========= 登录逻辑 =========
const router = useRouter()
const accountStore = useAccountStore()

const form = ref({ name: '', password: '' })
const formRef = ref()

const rules = {
    name: [{ required: true, message: '账户名不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const goMailbox = () => {
    window.open('/mailbox/login', '_blank')
}

const handleLogin = () => {
    formRef.value.validate(async (valid) => {
        if (!valid) return
        const { data } = await request.post('/account/login', form.value)
        if (data.code === 200) {
            accountStore.setAccount(data.data)
            ElMessage.success('登录成功')
            router.push('/dashboard')
        }
    })
}
</script>

<style src="@/assets/css/auth.css"></style>
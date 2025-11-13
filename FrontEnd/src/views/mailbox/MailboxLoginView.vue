<template>
    <div class="auth-container">

        <!-- 炫酷弹幕背景 -->
        <div class="danmu-container"></div>

        <el-card class="auth-card">
            <h2 class="auth-title">Mailbox</h2>

            <el-form class="big-form" :model="form" ref="formRef" :rules="rules" label-width="80px">
                <el-form-item label="联系人" prop="name">
                    <el-input v-model="form.name" placeholder="请输入联系人名" />
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" placeholder="请输入密码" show-password />
                </el-form-item>

                <el-form-item class="form-actions">
                    <el-button type="primary" @click="handleLogin">登录</el-button>
                </el-form-item>
            </el-form>
        </el-card>

    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useContactStore } from '../../store/contact'
import request from '../../api/request'

import { useDanmu } from '@/assets/js/danmu'
useDanmu()

const router = useRouter()
const contactStore = useContactStore()

const form = ref({ name: '', password: '' })
const formRef = ref()

const rules = {
    name: [{ required: true, message: '联系人名不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleLogin = () => {
    formRef.value.validate(async valid => {
        if (!valid) return
        const { data } = await request.post('/mailbox/login', form.value)
        if (data.code === 200) {
            contactStore.setContact(data.data)
            ElMessage.success('登录成功')
            router.push('/mailbox/reserve')
        } else {
            ElMessage.error(data.msg || '登录失败')
        }
    })
}
</script>

<style src="@/assets/css/auth.css"></style>
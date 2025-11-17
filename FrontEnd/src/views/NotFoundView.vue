<template>
    <div class="notfound-container">

        <!-- 炫酷弹幕背景 -->
        <div class="danmu-container"></div>

        <div class="box">
            <h1>404</h1>
            <p>抱歉，你访问的页面不存在。</p>
            <p class="sub">可能是路径输错了，或者链接已失效。</p>

            <el-button type="primary" @click="goHome">
                返回首页
            </el-button>
        </div>
    </div>
</template>

<script setup>
import { useDanmu } from '@/assets/js/danmu'
useDanmu()

import { useRouter } from 'vue-router'
const router = useRouter()

const goHome = () => {
    router.push('/login')
}
</script>

<style scoped>
.notfound-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1e2b3a 0%, #0a74de 100%);
    color: #fff;
    text-align: center;
}

.box {
    padding: 40px 50px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    backdrop-filter: blur(10px);
    animation: float 3s ease-in-out infinite;
}

h1 {
    font-size: 120px;
    font-weight: 700;
    margin: 0;
    color: #ffd04b;
}

p {
    font-size: 20px;
    margin: 10px 0;
}

.sub {
    font-size: 16px;
    opacity: 0.8;
    margin-bottom: 25px;
}

@keyframes float {
    0%, 100% {
        transform: translateY(-3px);
    }
    50% {
        transform: translateY(6px);
    }
}
</style>
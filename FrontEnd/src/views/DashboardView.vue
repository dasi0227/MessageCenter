<template>
    <div class="dashboard">
        <!-- 顶部统计卡片 -->
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="4" v-for="item in totalList" :key="item.label">
                <el-card class="stat-card">
                    <div class="label">{{ item.label }}</div>
                    <div class="value">{{ item.value }}</div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts">
            <!-- 渠道分布 -->
            <el-col :span="12">
                <el-card><div ref="channelChart" class="chart"></div></el-card>
            </el-col>

            <!-- 账户排行 -->
            <el-col :span="12">
                <el-card><div ref="accountChart" class="chart"></div></el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts">
            <!-- 联系人状态饼图 -->
            <el-col :span="12">
                <el-card><div ref="contactChart" class="chart"></div></el-card>
            </el-col>

            <!-- 年度趋势 -->
            <el-col :span="12">
                <el-card><div ref="yearChart" class="chart"></div></el-card>
            </el-col>
        </el-row>

        <el-row>
            <!-- 月度折线图 -->
            <el-col :span="24">
                <el-card><div ref="monthChart" class="chart"></div></el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../api/request'

const yearChart = ref()
const channelChart = ref()
const accountChart = ref()
const contactChart = ref()
const monthChart = ref()

const totalList = ref([])

const drawCharts = (data) => {
    // 年度趋势
    const yc = echarts.init(yearChart.value)
    yc.setOption({
        title: { text: '年度发送趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { data: data.year.months },
        yAxis: {},
        series: [{ name: '消息数', type: 'bar', data: data.year.counts, color: '#409EFF' }]
    })

    // 渠道分布
    const cc = echarts.init(channelChart.value)
    cc.setOption({
        title: { text: '渠道发送量排行' },
        tooltip: { trigger: 'item' },
        xAxis: { type: 'category', data: data.channel.channels },
        yAxis: {},
        series: [{ type: 'bar', data: data.channel.counts, color: '#67C23A' }]
    })

    // 账户发送量排行
    const ac = echarts.init(accountChart.value)
    ac.setOption({
        title: { text: '账户发送量排行' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.account.accounts },
        yAxis: {},
        series: [{ type: 'bar', data: data.account.counts, color: '#E6A23C' }]
    })

    // 联系人活跃状态饼图
    const ct = echarts.init(contactChart.value)
    ct.setOption({
        title: { text: '联系人活跃分布' },
        tooltip: { trigger: 'item' },
        series: [
            {
                type: 'pie',
                radius: '60%',
                data: [
                    { name: '活跃', value: data.contact.activeContacts },
                    { name: '不活跃', value: data.contact.inactiveContacts }
                ]
            }
        ]
    })

    // 月度折线趋势
    const mc = echarts.init(monthChart.value)
    mc.setOption({
        title: { text: '本月每日发送趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { data: data.month.days },
        yAxis: {},
        series: [{ type: 'line', smooth: true, data: data.month.counts, color: '#F56C6C' }]
    })

    // 自适应
    window.addEventListener('resize', () => {
        yc.resize()
        cc.resize()
        ac.resize()
        ct.resize()
        mc.resize()
    })
}

// 页面加载时获取数据
onMounted(async () => {
    const [total, contact, channel, account, year, month] = await Promise.all([
        request.get('/dashboard/total'),
        request.get('/dashboard/contact'),
        request.get('/dashboard/channel'),
        request.get('/dashboard/account'),
        request.get('/dashboard/year'),
        request.get('/dashboard/month')
    ])

    // ✅ 注意这里要用 data.data 取出真正的数据体
    totalList.value = [
        { label: '消息总数', value: total.data.data.messageTotal },
        { label: '发送总数', value: total.data.data.dispatchTotal },
        { label: '待发送', value: total.data.data.dispatchPending },
        { label: '发送中', value: total.data.data.dispatchSending },
        { label: '成功', value: total.data.data.dispatchSuccess },
        { label: '失败', value: total.data.data.dispatchFail }
    ]

    await nextTick()
    drawCharts({
        year: year.data.data,
        channel: channel.data.data,
        account: account.data.data,
        contact: contact.data.data,
        month: month.data.data
    })
})
</script>

<style scoped>
.dashboard {
    padding: 20px;
}
.stat-cards {
    margin-bottom: 30px;
}
.stat-card {
    text-align: center;
    background-color: #fff;
    border-radius: 8px;
    transition: all 0.2s;
}
.stat-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.label {
    font-size: 14px;
    color: #888;
}
.value {
    font-size: 22px;
    font-weight: 600;
    margin-top: 8px;
}
.chart {
    width: 100%;
    height: 360px;
}
.el-row + .el-row {
    margin-top: 30px;
}
</style>
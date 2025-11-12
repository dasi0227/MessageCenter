<template>
    <div class="dashboard">
        <!-- 顶部统计卡片 -->
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="4" v-for="item in statList" :key="item.label">
                <el-card class="stat-card">
                    <div class="label">{{ item.label }}</div>
                    <div class="value">{{ item.value }}</div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 分发维度统计 -->
        <el-row :gutter="20" class="charts">
            <el-col :span="12"><el-card><div ref="accountChart" class="chart"></div></el-card></el-col>
            <el-col :span="12"><el-card><div ref="channelChart" class="chart"></div></el-card></el-col>
        </el-row>

        <el-row :gutter="20" class="charts">
            <el-col :span="12"><el-card><div ref="departmentChart" class="chart"></div></el-card></el-col>
            <el-col :span="12"><el-card><div ref="contactChart" class="chart"></div></el-card></el-col>
        </el-row>

        <!-- 年度 & 月度趋势 -->
        <el-row :gutter="20" class="charts">
            <el-col :span="12"><el-card><div ref="yearChart" class="chart"></div></el-card></el-col>
            <el-col :span="12"><el-card><div ref="monthChart" class="chart"></div></el-card></el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../api/request'

/* refs */
const accountChart = ref()
const departmentChart = ref()
const contactChart = ref()
const channelChart = ref()
const yearChart = ref()
const monthChart = ref()

/* 顶部统计卡片 */
const statList = ref([])

/* 绘制函数 */
const drawCharts = (dispatch, timeline) => {
    const baseOpt = { tooltip: { trigger: 'axis' }, xAxis: {}, yAxis: {} }

    const ac = echarts.init(accountChart.value)
    ac.setOption({
        ...baseOpt,
        title: { text: '账户处理量' },
        xAxis: { type: 'category', data: dispatch.accountNames },
        series: [{ type: 'bar', data: dispatch.accountCounts, color: '#409EFF' }]
    })

    const dc = echarts.init(departmentChart.value)
    dc.setOption({
        ...baseOpt,
        title: { text: '部门发送量' },
        xAxis: { type: 'category', data: dispatch.departmentNames },
        series: [{ type: 'bar', data: dispatch.departmentCounts, color: '#67C23A' }]
    })

    const cc = echarts.init(contactChart.value)
    cc.setOption({
        ...baseOpt,
        title: { text: '联系人接收量' },
        xAxis: { type: 'category', data: dispatch.contactNames },
        series: [{ type: 'bar', data: dispatch.contactCounts, color: '#E6A23C' }]
    })

    const ch = echarts.init(channelChart.value)
    ch.setOption({
        ...baseOpt,
        title: { text: '渠道发送量' },
        xAxis: { type: 'category', data: dispatch.channelNames },
        series: [{ type: 'bar', data: dispatch.channelCounts, color: '#F56C6C' }]
    })

    const yc = echarts.init(yearChart.value)
    yc.setOption({
        title: { text: '年度发送趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { data: timeline.months },
        yAxis: {},
        series: [{ type: 'line', smooth: true, data: timeline.monthCounts, color: '#C39DA9' }]
    })

    const mc = echarts.init(monthChart.value)
    mc.setOption({
        title: { text: '本月发送趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { data: timeline.days },
        yAxis: {},
        series: [{ type: 'line', smooth: true, data: timeline.dayCounts, color: '#A15EFF' }]
    })

    window.addEventListener('resize', () => {
        ac.resize()
        dc.resize()
        cc.resize()
        ch.resize()
        yc.resize()
        mc.resize()
    })
}

/* 初始化加载 */
onMounted(async () => {
    const [numRes, dispatchRes, timelineRes] = await Promise.all([
        request.get('/dashboard/num'),
        request.get('/dashboard/dispatch'),
        request.get('/dashboard/timeline')
    ])

    const n = numRes.data.data
    statList.value = [
        { label: '消息总数', value: n.messageTotal },
        { label: '投递总数', value: n.dispatchTotal },
        { label: '待发送', value: n.dispatchPending },
        { label: '发送中', value: n.dispatchSending },
        { label: '成功', value: n.dispatchSuccess },
        { label: '失败', value: n.dispatchFail },
        { label: '账户数', value: n.accountNum },
        { label: '部门数', value: n.departmentNum },
        { label: '联系人数', value: n.contactNum },
        { label: '敏感词数', value: n.sensitiveWordNum },
        { label: '模板数', value: n.templateNum },
        { label: '占位符数', value: n.renderNum }
    ]

    await nextTick()
    drawCharts(dispatchRes.data.data, timelineRes.data.data)
})
</script>

<style scoped>
.dashboard {
    padding: 20px;
}
.stat-cards {
    margin-bottom: 30px;
    row-gap: 30px;
    flex-wrap: wrap;
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
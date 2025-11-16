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
            <el-col :span="12">
                <el-card><div ref="accountChart" class="chart"></div></el-card>
            </el-col>
            <el-col :span="12">
                <el-card><div ref="channelChart" class="chart"></div></el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts">
            <el-col :span="12">
                <el-card><div ref="departmentChart" class="chart"></div></el-card>
            </el-col>
            <el-col :span="12">
                <el-card><div ref="contactChart" class="chart"></div></el-card>
            </el-col>
        </el-row>

        <!-- 年度 & 月度趋势 -->
        <el-row :gutter="20" class="charts">
            <el-col :span="12">
                <el-card><div ref="yearChart" class="chart"></div></el-card>
            </el-col>
            <el-col :span="12">
                <el-card><div ref="monthChart" class="chart"></div></el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import 'echarts/theme/dark'          // 关键：引入 dark 主题
import request from '../api/request'

/* dom refs */
const accountChart = ref()
const departmentChart = ref()
const contactChart = ref()
const channelChart = ref()
const yearChart = ref()
const monthChart = ref()

/* 顶部统计卡片 */
const statList = ref([])

/* 持久保存数据，方便主题切换时重绘 */
const dispatchData = ref(null)
const timelineData = ref(null)

/* 保存所有图表实例 */
let chartInstances = []

/* 判断是否暗色模式（看 html 上有没有 .dark） */
const isDark = () => document.documentElement.classList.contains('dark')

/* 创建图表 */
const createChart = (dom, option) => {
    if (!dom) return null
    const theme = isDark() ? 'dark' : null
    const chart = echarts.init(dom, theme)
    chart.setOption(option)
    chartInstances.push(chart)
    return chart
}

/* 销毁所有图表 */
const disposeCharts = () => {
    chartInstances.forEach(c => c && !c.isDisposed() && c.dispose())
    chartInstances = []
}

/* 统一绘制所有图表 */
const drawCharts = (dispatch, timeline) => {
    if (!dispatch || !timeline) return

    disposeCharts()

    const style = getComputedStyle(document.documentElement)
    const color = style.getPropertyValue('--el-text-color-primary').trim()

    const baseOpt = {
        backgroundColor: 'transparent',
        textStyle: { color: color },
        tooltip: { trigger: 'axis' },
        xAxis: {
            type: 'category',
            axisLine: { lineStyle: { color: color } },
            axisLabel: { color: color }
        },
        yAxis: {
            type: 'value',
            axisLine: { lineStyle: { color: color } },
            axisLabel: { color: color },
            splitLine: { lineStyle: { color: color, opacity: 0.5 } }
        }
    }

    createChart(accountChart.value, {
        ...baseOpt,
        title: { text: '账户处理量', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: dispatch.accountNames },
        series: [{ type: 'bar', data: dispatch.accountCounts, itemStyle: { color: '#409EFF' } }]
    })

    createChart(departmentChart.value, {
        ...baseOpt,
        title: { text: '部门发送量', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: dispatch.departmentNames },
        series: [{ type: 'bar', data: dispatch.departmentCounts, itemStyle: { color: '#67C23A' } }]
    })

    createChart(contactChart.value, {
        ...baseOpt,
        title: { text: '联系人接收量', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: dispatch.contactNames },
        series: [{ type: 'bar', data: dispatch.contactCounts, itemStyle: { color: '#E6A23C' } }]
    })

    createChart(channelChart.value, {
        ...baseOpt,
        title: { text: '渠道发送量', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: dispatch.channelNames },
        series: [{ type: 'bar', data: dispatch.channelCounts, itemStyle: { color: '#F56C6C' } }]
    })

    createChart(yearChart.value, {
        ...baseOpt,
        title: { text: '年度发送趋势', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: timeline.months },
        series: [{
            type: 'line',
            smooth: true,
            data: timeline.monthCounts,
            lineStyle: { color: '#C39DA9' },
            itemStyle: { color: '#C39DA9' }
        }]
    })

    createChart(monthChart.value, {
        ...baseOpt,
        title: { text: '本月发送趋势', textStyle: { color: color } },
        xAxis: { ...baseOpt.xAxis, data: timeline.days },
        series: [{
            type: 'line',
            smooth: true,
            data: timeline.dayCounts,
            lineStyle: { color: '#A15EFF' },
            itemStyle: { color: '#A15EFF' }
        }]
    })
}

/* 窗口缩放时自适应 */
const handleResize = () => {
    chartInstances.forEach(c => c && !c.isDisposed() && c.resize())
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

    dispatchData.value = dispatchRes.data.data
    timelineData.value = timelineRes.data.data

    await nextTick()
    drawCharts(dispatchData.value, timelineData.value)

    window.addEventListener('resize', handleResize)
    // 监听全局主题切换事件（在 Layout 的 toggleTheme 里触发）
    window.addEventListener('app-theme-change', async () => {
        await nextTick()
        drawCharts(dispatchData.value, timelineData.value)
    })
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    disposeCharts()
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
    border-radius: 8px;
    transition: all 0.2s;
}
.stat-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.label {
    font-size: 14px;
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
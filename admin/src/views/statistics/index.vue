<template>
  <div>
    <div class="page-card">
      <div class="page-card-title" style="display:flex;justify-content:space-between;align-items:center">
        <span>统计报表</span>
        <div>
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="margin-right:12px" />
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleExport">导出报表</el-button>
        </div>
      </div>

      <!-- Industry table -->
      <el-table :data="industryData" stripe border style="margin-bottom:24px">
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="merchantCount" label="商户数" width="90" />
        <el-table-column prop="orderCount" label="订单数" width="90" />
        <el-table-column label="托管金额(万元)" width="130"><template #default="{ row }">{{ (row.escrowAmount / 10000).toFixed(1) }}</template></el-table-column>
        <el-table-column label="退款率" width="90"><template #default="{ row }"><span :style="{ color: row.refundRate > 5 ? '#f56c6c' : '#67c23a' }">{{ row.refundRate }}%</span></template></el-table-column>
        <el-table-column prop="complaintCount" label="投诉数" width="90" />
      </el-table>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="page-card">
          <div class="page-card-title">月度订单趋势</div>
          <div ref="trendChartRef" style="height:350px"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card">
          <div class="page-card-title">月度资金趋势</div>
          <div ref="amountChartRef" style="height:350px"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { fetchStatistics } from '@/api'
import { ElMessage } from 'element-plus'

const dateRange = ref([])
const industryData = ref([])
const trendChartRef = ref(null)
const amountChartRef = ref(null)

async function loadData() {
  const res = await fetchStatistics({})
  industryData.value = res.industryData || []
  initCharts(res.monthlyTrend || [])
}

function initCharts(trend) {
  const months = trend.map(t => t.month)
  // Trend chart
  const c1 = echarts.init(trendChartRef.value)
  c1.setOption({
    tooltip: { trigger: 'axis' }, legend: { data: ['订单数', '退款数'] },
    grid: { left: 50, right: 20, bottom: 30, top: 40 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [
      { name: '订单数', type: 'line', data: trend.map(t => t.orders), smooth: true, itemStyle: { color: '#1a3c6e' }, areaStyle: { color: 'rgba(26,60,110,0.1)' } },
      { name: '退款数', type: 'line', data: trend.map(t => t.refunds), smooth: true, itemStyle: { color: '#c41e3a' } }
    ]
  })
  // Amount chart
  const c2 = echarts.init(amountChartRef.value)
  c2.setOption({
    tooltip: { trigger: 'axis' }, legend: { data: ['托管金额'] },
    grid: { left: 60, right: 20, bottom: 30, top: 40 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', axisLabel: { formatter: v => (v / 10000).toFixed(0) + '万' } },
    series: [
      { name: '托管金额', type: 'bar', data: trend.map(t => t.amount), itemStyle: { color: '#1a3c6e', borderRadius: [4, 4, 0, 0] } }
    ]
  })
  window.addEventListener('resize', () => { c1.resize(); c2.resize() })
}

function handleExport() { ElMessage.success('报表导出功能（模拟）：报表文件已生成') }

onMounted(loadData)
</script>

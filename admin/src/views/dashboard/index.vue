<template>
  <div>
    <!-- Stats row -->
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6" v-for="s in statCards" :key="s.label">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: s.color }">
            <el-icon><component :is="s.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ s.value }}</h3>
            <p>{{ s.label }}</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts row -->
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="14">
        <div class="page-card">
          <div class="page-card-title">近期订单趋势</div>
          <div ref="orderChartRef" style="height:320px"></div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="page-card">
          <div class="page-card-title">行业分布</div>
          <div ref="pieChartRef" style="height:320px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- Alerts -->
    <div class="page-card">
      <div class="page-card-title">最新风险预警</div>
      <el-table :data="alerts" stripe size="small" v-loading="alertsLoading">
        <el-table-column prop="merchantName" label="商户名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="type" label="预警类型" width="120" align="center" />
        <el-table-column label="风险等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="{ high:'danger', medium:'warning', low:'success' }[row.severity]" size="small" effect="dark">{{ { high: '高', medium: '中', low: '低' }[row.severity] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="预警内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="160" align="center" />
        <template #empty>
          <el-empty description="暂无风险预警" :image-size="80" />
        </template>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, markRaw } from 'vue'
import * as echarts from 'echarts'
import { fetchDashboardStats, fetchRecentAlerts } from '@/api'

const orderChartRef = ref(null)
const pieChartRef = ref(null)
const alerts = ref([])
const alertsLoading = ref(false)
const statCards = reactive([
  { label: '入驻商户', value: 0, icon: 'OfficeBuilding', color: '#1a3c6e' },
  { label: '总订单数', value: 0, icon: 'Document', color: '#409eff' },
  { label: '托管资金(万元)', value: 0, icon: 'Money', color: '#67c23a' },
  { label: '待审核', value: 0, icon: 'Bell', color: '#c41e3a' }
])

onMounted(async () => {
  const stats = await fetchDashboardStats()
  statCards[0].value = stats.totalMerchants
  statCards[1].value = stats.totalOrders
  statCards[2].value = (stats.escrowAmount / 10000).toFixed(1)
  statCards[3].value = stats.pendingReviews

  alertsLoading.value = true
  try { alerts.value = await fetchRecentAlerts() } finally { alertsLoading.value = false }
  initOrderChart()
  initPieChart()
})

function initOrderChart() {
  const chart = echarts.init(orderChartRef.value)
  const months = []; const data1 = []; const data2 = []
  for (let i = 5; i >= 0; i--) {
    const d = new Date(); d.setMonth(d.getMonth() - i)
    months.push(d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'))
    data1.push(Math.floor(Math.random() * 200) + 100)
    data2.push(Math.floor(Math.random() * 50) + 20)
  }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '退款数'] },
    grid: { left: 50, right: 20, bottom: 30, top: 40 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [
      { name: '订单数', type: 'bar', data: data1, itemStyle: { color: '#1a3c6e' } },
      { name: '退款数', type: 'line', data: data2, itemStyle: { color: '#c41e3a' } }
    ]
  })
  window.addEventListener('resize', () => chart.resize())
}

function initPieChart() {
  const chart = echarts.init(pieChartRef.value)
  const data = ['教育培训', '健身美容', '家政服务', '餐饮服务', '医疗健康', '零售百货'].map(n => ({ name: n, value: Math.floor(Math.random() * 100) + 20 }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', right: 10, top: 'center' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'],
      data,
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } },
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}
</script>

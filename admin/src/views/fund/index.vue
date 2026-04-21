<template>
  <div>
    <!-- Overview cards -->
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6" v-for="s in overviewCards" :key="s.label">
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

    <el-tabs v-model="activeTab">
      <el-tab-pane label="交易记录" name="records" />
      <el-tab-pane label="资金释放" name="release" />
      <el-tab-pane label="资金统计" name="stats" />
    </el-tabs>

    <!-- Records tab -->
    <div class="page-card" v-if="activeTab==='records'">
      <div class="filter-bar">
        <el-input v-model="filters.keyword" placeholder="商户/订单号" clearable style="width:200px" @clear="loadRecords" @keyup.enter="loadRecords" />
        <el-select v-model="filters.type" placeholder="类型" clearable style="width:130px" @change="loadRecords">
          <el-option label="托管入账" value="escrow_in" /><el-option label="资金释放" value="release" />
        </el-select>
        <el-button type="primary" @click="loadRecords">查询</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="id" label="编号" width="100" />
        <el-table-column prop="orderId" label="订单号" width="120" />
        <el-table-column prop="merchantName" label="商户" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }"><el-tag :type="row.type==='escrow_in'?'':'success'" size="small">{{ row.type==='escrow_in'?'托管入账':'资金释放' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="金额" width="120"><template #default="{ row }"><span :style="{color:row.type==='escrow_in'?'#67c23a':'#409eff'}">{{ row.type==='escrow_in'?'+':'-' }}¥{{ row.amount?.toLocaleString() }}</span></template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="110" />
      </el-table>
    </div>

    <!-- Release tab -->
    <div class="page-card" v-if="activeTab==='release'">
      <el-alert title="资金释放操作：选择已完成服务的订单，审核后释放托管资金给商户" type="info" :closable="false" style="margin-bottom:16px" />
      <el-form :inline="true" :model="releaseForm" label-width="80px">
        <el-form-item label="订单号"><el-input v-model="releaseForm.orderId" placeholder="输入订单号" /></el-form-item>
        <el-form-item label="释放金额"><el-input-number v-model="releaseForm.amount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="releaseForm.remark" placeholder="释放原因" /></el-form-item>
        <el-form-item><el-button type="primary" @click="doRelease">确认释放</el-button></el-form-item>
      </el-form>
    </div>

    <!-- Stats tab -->
    <div class="page-card" v-if="activeTab==='stats'">
      <div ref="fundChartRef" style="height:400px"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { fetchFundOverview, fetchFundRecords, releaseFund } from '@/api'
import { ElMessage } from 'element-plus'

const activeTab = ref('records')
const loading = ref(false)
const records = ref([])
const filters = reactive({ keyword: '', type: '' })
const releaseForm = reactive({ orderId: '', amount: 0, remark: '' })
const fundChartRef = ref(null)

const overviewCards = reactive([
  { label: '托管总额(万元)', value: '0', icon: 'Money', color: '#1a3c6e' },
  { label: '已释放(万元)', value: '0', icon: 'CircleCheck', color: '#67c23a' },
  { label: '已退款(万元)', value: '0', icon: 'RefreshLeft', color: '#e6a23c' },
  { label: '待释放笔数', value: '0', icon: 'Clock', color: '#c41e3a' }
])

onMounted(async () => {
  const ov = await fetchFundOverview()
  overviewCards[0].value = (ov.totalEscrow / 10000).toFixed(1)
  overviewCards[1].value = (ov.totalReleased / 10000).toFixed(1)
  overviewCards[2].value = (ov.totalRefunded / 10000).toFixed(1)
  overviewCards[3].value = String(ov.pendingRelease)
  loadRecords()
})

async function loadRecords() {
  loading.value = true
  try { const res = await fetchFundRecords(filters); records.value = res.list || res } finally { loading.value = false }
}

async function doRelease() {
  if (!releaseForm.orderId) { ElMessage.warning('请输入订单号'); return }
  await releaseFund(releaseForm)
  ElMessage.success('资金释放成功')
  releaseForm.orderId = ''; releaseForm.amount = 0; releaseForm.remark = ''
}

watch(activeTab, async (val) => {
  if (val === 'stats') {
    await nextTick()
    const chart = echarts.init(fundChartRef.value)
    const months = []; const inData = []; const outData = []
    for (let i = 5; i >= 0; i--) {
      const d = new Date(); d.setMonth(d.getMonth() - i)
      months.push(d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'))
      inData.push(Math.floor(Math.random() * 500000) + 200000)
      outData.push(Math.floor(Math.random() * 300000) + 100000)
    }
    chart.setOption({
      tooltip: { trigger: 'axis' }, legend: { data: ['托管入账', '资金释放'] },
      grid: { left: 60, right: 20, bottom: 30, top: 40 },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', axisLabel: { formatter: v => (v / 10000).toFixed(0) + '万' } },
      series: [
        { name: '托管入账', type: 'bar', data: inData, itemStyle: { color: '#1a3c6e' } },
        { name: '资金释放', type: 'bar', data: outData, itemStyle: { color: '#67c23a' } }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }
})
</script>

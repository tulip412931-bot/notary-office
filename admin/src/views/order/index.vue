<template>
  <div class="page-card">
    <div class="page-card-title">订单管理</div>
    <div class="filter-bar">
      <el-input v-model="filters.keyword" placeholder="订单号/商户/客户" clearable style="width:220px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="filters.status" placeholder="订单状态" clearable style="width:130px" @change="loadData">
        <el-option v-for="(v,k) in statusLabels" :key="k" :label="v" :value="k" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="merchantName" label="商户" min-width="150" show-overflow-tooltip />
      <el-table-column prop="productName" label="产品" min-width="140" show-overflow-tooltip />
      <el-table-column prop="customerName" label="客户" width="80" />
      <el-table-column label="金额" width="100"><template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="托管金额" width="110"><template #default="{ row }">¥{{ row.escrowAmount?.toLocaleString() }}</template></el-table-column>
      <el-table-column prop="payMethod" label="支付方式" width="90" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="orderTagType(row.status)" size="small">{{ statusLabels[row.status] || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="110" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Detail dialog -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <template v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="orderTagType(currentOrder.status)" size="small">{{ statusLabels[currentOrder.status] }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="商户">{{ currentOrder.merchantName }}</el-descriptions-item>
          <el-descriptions-item label="产品">{{ currentOrder.productName }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ currentOrder.customerName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.amount?.toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="托管金额">¥{{ currentOrder.escrowAmount?.toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.payMethod }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createdAt }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top:24px">
          <h4 style="margin-bottom:12px;color:var(--primary)">托管状态时间线</h4>
          <el-timeline>
            <el-timeline-item timestamp="订单创建" placement="top" color="#1a3c6e">
              <p>{{ currentOrder.createdAt }} 用户下单</p>
            </el-timeline-item>
            <el-timeline-item v-if="currentOrder.paidAt" timestamp="支付完成" placement="top" color="#67c23a">
              <p>{{ currentOrder.paidAt }} 支付成功，资金进入托管账户</p>
            </el-timeline-item>
            <el-timeline-item v-if="currentOrder.status==='in_service'||currentOrder.status==='completed'" timestamp="服务中" placement="top" color="#409eff">
              <p>商户开始提供服务</p>
            </el-timeline-item>
            <el-timeline-item v-if="currentOrder.status==='completed'" timestamp="已完成" placement="top" color="#67c23a">
              <p>服务完成，托管资金释放给商户</p>
            </el-timeline-item>
            <el-timeline-item v-if="currentOrder.status==='refunding'||currentOrder.status==='refunded'" timestamp="退款" placement="top" color="#e6a23c">
              <p>{{ currentOrder.status==='refunded'?'退款完成':'退款处理中' }}</p>
            </el-timeline-item>
            <el-timeline-item v-if="currentOrder.status==='cancelled'" timestamp="已取消" placement="top" color="#909399">
              <p>订单已取消</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchOrders, ORDER_STATUS_LABEL } from '@/api'

const statusLabels = ORDER_STATUS_LABEL
const loading = ref(false)
const tableData = ref([])
const filters = reactive({ keyword: '', status: '' })
const detailVisible = ref(false)
const currentOrder = ref(null)

function orderTagType(s) { return { pending_pay: 'info', paid: '', in_service: 'warning', completed: 'success', refunding: 'warning', refunded: 'danger', cancelled: 'info' }[s] || '' }

async function loadData() {
  loading.value = true
  try { const res = await fetchOrders(filters); tableData.value = res.list || res } finally { loading.value = false }
}

function showDetail(row) { currentOrder.value = row; detailVisible.value = true }

onMounted(loadData)
</script>

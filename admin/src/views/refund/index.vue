<template>
  <div class="page-card">
    <div class="page-card-title">退款管理</div>
    <div class="filter-bar">
      <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width:130px" @change="loadData">
        <el-option label="待处理" value="pending" /><el-option label="已通过" value="approved" /><el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="退款编号" width="100" align="center" />
      <el-table-column prop="orderId" label="订单号" width="120" />
      <el-table-column prop="merchantName" label="商户" min-width="150" show-overflow-tooltip />
      <el-table-column prop="customerName" label="客户" width="80" />
      <el-table-column label="订单金额" width="100" align="right"><template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="退款金额" width="100" align="right"><template #default="{ row }"><span style="color:#c41e3a;font-weight:600">¥{{ row.refundAmount?.toLocaleString() }}</span></template></el-table-column>
      <el-table-column prop="reason" label="退款原因" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="{ pending:'warning', approved:'success', rejected:'danger' }[row.status]" size="small">{{ { pending:'待处理', approved:'已通过', rejected:'已拒绝' }[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请日期" width="110" align="center" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openProcess(row)" v-if="row.status==='pending'">处理</el-button>
          <span v-else style="color:#999;font-size:12px">已处理</span>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无退款申请" :image-size="80" />
      </template>
    </el-table>

    <el-dialog v-model="processVisible" title="处理退款申请" width="480px">
      <el-descriptions :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="退款编号">{{ processTarget?.id }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ processTarget?.customerName }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ processTarget?.refundAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="退款原因">{{ processTarget?.reason }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="80px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="processForm.status">
            <el-radio value="approved">通过</el-radio><el-radio value="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input v-model="processForm.note" type="textarea" :rows="3" placeholder="请输入处理意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible=false">取消</el-button>
        <el-button type="primary" @click="submitProcess">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchRefunds, processRefund } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const filters = reactive({ status: '' })
const processVisible = ref(false)
const processTarget = ref(null)
const processForm = reactive({ status: 'approved', note: '' })

async function loadData() {
  loading.value = true
  try { const res = await fetchRefunds(filters); tableData.value = res.list || res } finally { loading.value = false }
}

function openProcess(row) { processTarget.value = row; processForm.status = 'approved'; processForm.note = ''; processVisible.value = true }

async function submitProcess() {
  await processRefund(processTarget.value.id, processForm)
  ElMessage.success('处理完成')
  processVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

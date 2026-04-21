<template>
  <div class="page-card">
    <div class="page-card-title">投诉管理</div>
    <div class="filter-bar">
      <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width:130px" @change="loadData">
        <el-option label="待处理" value="pending" /><el-option label="处理中" value="processing" /><el-option label="已解决" value="resolved" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="编号" width="80" align="center" />
      <el-table-column prop="merchantName" label="被投诉商户" min-width="160" show-overflow-tooltip />
      <el-table-column prop="customerName" label="投诉人" width="80" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="type" label="投诉类型" width="100" align="center" />
      <el-table-column prop="content" label="投诉内容" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="{ pending:'warning', processing:'', resolved:'success' }[row.status]" size="small">{{ { pending:'待处理', processing:'处理中', resolved:'已解决' }[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="投诉日期" width="110" align="center" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openHandle(row)" v-if="row.status!=='resolved'">处理</el-button>
          <span v-else style="color:#999;font-size:12px">已解决</span>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无投诉记录" :image-size="80" />
      </template>
    </el-table>

    <el-dialog v-model="handleVisible" title="处理投诉" width="500px">
      <el-descriptions :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="投诉人">{{ handleTarget?.customerName }}</el-descriptions-item>
        <el-descriptions-item label="被投诉商户">{{ handleTarget?.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="投诉类型">{{ handleTarget?.type }}</el-descriptions-item>
        <el-descriptions-item label="投诉内容">{{ handleTarget?.content }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="80px">
        <el-form-item label="处理状态">
          <el-radio-group v-model="handleForm.status">
            <el-radio value="processing">处理中</el-radio><el-radio value="resolved">已解决</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="handleForm.note" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible=false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchComplaints, handleComplaint } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const filters = reactive({ status: '' })
const handleVisible = ref(false)
const handleTarget = ref(null)
const handleForm = reactive({ status: 'processing', note: '' })

async function loadData() {
  loading.value = true
  try { const res = await fetchComplaints(filters); tableData.value = res.list || res } finally { loading.value = false }
}

function openHandle(row) { handleTarget.value = row; handleForm.status = 'processing'; handleForm.note = row.handleNote || ''; handleVisible.value = true }

async function submitHandle() {
  await handleComplaint(handleTarget.value.id, handleForm)
  ElMessage.success('处理完成')
  handleVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

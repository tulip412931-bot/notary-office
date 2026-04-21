<template>
  <div class="page-card">
    <div class="page-card-title">风险预警</div>
    <div class="filter-bar">
      <el-select v-model="filters.severity" placeholder="风险等级" clearable style="width:130px" @change="loadData">
        <el-option label="高" value="high" /><el-option label="中" value="medium" /><el-option label="低" value="low" />
      </el-select>
      <el-select v-model="filters.status" placeholder="处理状态" clearable style="width:130px" @change="loadData">
        <el-option label="待处理" value="pending" /><el-option label="处理中" value="processing" /><el-option label="已解决" value="resolved" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="编号" width="80" align="center" />
      <el-table-column prop="merchantName" label="商户" min-width="160" show-overflow-tooltip />
      <el-table-column prop="type" label="预警类型" width="100" align="center" />
      <el-table-column label="风险等级" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="{ high:'danger', medium:'warning', low:'success' }[row.severity]" size="small" effect="dark">{{ { high:'高', medium:'中', low:'低' }[row.severity] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="预警内容" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="{ pending:'danger', processing:'warning', resolved:'success' }[row.status]" size="small">{{ { pending:'待处理', processing:'处理中', resolved:'已解决' }[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="预警时间" width="110" align="center" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openHandle(row)" v-if="row.status!=='resolved'">处理</el-button>
          <span v-else style="color:#999;font-size:12px">已解决</span>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无风险预警" :image-size="80" />
      </template>
    </el-table>

    <el-dialog v-model="handleVisible" title="处理风险预警" width="500px">
      <el-alert :title="handleTarget?.content" :type="handleTarget?.severity==='high'?'error':'warning'" :closable="false" style="margin-bottom:16px" />
      <el-descriptions :column="1" border style="margin-bottom:16px">
        <el-descriptions-item label="商户">{{ handleTarget?.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="预警类型">{{ handleTarget?.type }}</el-descriptions-item>
        <el-descriptions-item label="风险等级"><span :class="'severity-'+handleTarget?.severity">{{ { high:'高', medium:'中', low:'低' }[handleTarget?.severity] }}</span></el-descriptions-item>
      </el-descriptions>
      <el-form label-width="80px">
        <el-form-item label="处理状态">
          <el-radio-group v-model="handleForm.status">
            <el-radio value="processing">处理中</el-radio><el-radio value="resolved">已解决</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="handleForm.note" type="textarea" :rows="3" placeholder="请输入处理措施" />
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
import { fetchRiskAlerts, handleRiskAlert } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const filters = reactive({ severity: '', status: '' })
const handleVisible = ref(false)
const handleTarget = ref(null)
const handleForm = reactive({ status: 'processing', note: '' })

async function loadData() {
  loading.value = true
  try { const res = await fetchRiskAlerts(filters); tableData.value = res.list || res } finally { loading.value = false }
}

function openHandle(row) { handleTarget.value = row; handleForm.status = 'processing'; handleForm.note = row.handleNote || ''; handleVisible.value = true }

async function submitHandle() {
  await handleRiskAlert(handleTarget.value.id, handleForm)
  ElMessage.success('处理完成')
  handleVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

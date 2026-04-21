<template>
  <div>
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="商户列表" name="list" />
      <el-tab-pane label="黑名单管理" name="blacklist" />
    </el-tabs>

    <div class="page-card">
      <div class="filter-bar">
        <el-input v-model="filters.keyword" placeholder="搜索商户名称" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="filters.industry" placeholder="行业筛选" clearable style="width:150px" @change="loadData">
          <el-option v-for="i in industries" :key="i" :label="i" :value="i" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态筛选" clearable style="width:130px" @change="loadData" v-if="activeTab==='list'">
          <el-option label="待审核" value="pending" /><el-option label="已通过" value="approved" /><el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="商户编号" width="90" align="center" />
        <el-table-column prop="name" label="商户名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="industry" label="行业" width="100" align="center" />
        <el-table-column prop="contact" label="联系人" width="80" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column label="托管余额" width="120" align="right">
          <template #default="{ row }">¥{{ row.escrowBalance?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column label="信用评分" width="90" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.creditScore >= 80 ? '#67c23a' : row.creditScore >= 60 ? '#e6a23c' : '#f56c6c', fontWeight: 600 }">{{ row.creditScore || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registeredAt" label="注册日期" width="110" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="openReview(row)" v-if="row.status==='pending'">审核</el-button>
            <el-button link type="danger" size="small" @click="doBlacklist(row)" v-if="row.status==='approved'">拉黑</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无商户数据" :image-size="80" />
        </template>
      </el-table>
    </div>

    <!-- Detail drawer -->
    <el-drawer v-model="drawerVisible" title="商户详情" size="480px">
      <template v-if="currentMerchant">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="商户编号">{{ currentMerchant.id }}</el-descriptions-item>
          <el-descriptions-item label="商户名称">{{ currentMerchant.name }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ currentMerchant.industry }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentMerchant.contact }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentMerchant.phone }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ currentMerchant.address }}</el-descriptions-item>
          <el-descriptions-item label="营业执照">{{ currentMerchant.license }}</el-descriptions-item>
          <el-descriptions-item label="托管余额">¥{{ currentMerchant.escrowBalance?.toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="信用评分">{{ currentMerchant.creditScore }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="statusType(currentMerchant.status)" size="small">{{ statusLabel(currentMerchant.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="注册日期">{{ currentMerchant.registeredAt }}</el-descriptions-item>
          <el-descriptions-item label="审核备注" v-if="currentMerchant.reviewNote">{{ currentMerchant.reviewNote }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>

    <!-- Review dialog -->
    <el-dialog v-model="reviewVisible" title="商户审核" width="480px">
      <p style="margin-bottom:12px">商户: <strong>{{ reviewTarget?.name }}</strong></p>
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.note" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReview">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchMerchants, reviewMerchant } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const industries = ['教育培训', '健身美容', '家政服务', '旅游出行', '餐饮服务', '汽车服务', '医疗健康', '零售百货']
const activeTab = ref('list')
const loading = ref(false)
const tableData = ref([])
const filters = reactive({ keyword: '', industry: '', status: '' })

const drawerVisible = ref(false)
const currentMerchant = ref(null)
const reviewVisible = ref(false)
const reviewTarget = ref(null)
const reviewForm = reactive({ status: 'approved', note: '' })

function statusLabel(s) { return { pending: '待审核', approved: '已通过', rejected: '已拒绝', blacklisted: '已拉黑' }[s] || s }
function statusType(s) { return { pending: 'warning', approved: 'success', rejected: 'danger', blacklisted: 'info' }[s] || '' }

async function loadData() {
  loading.value = true
  try {
    const params = { ...filters }
    if (activeTab.value === 'blacklist') { params.status = 'blacklisted'; delete params.industry }
    const res = await fetchMerchants(params)
    tableData.value = res.list || res
  } finally { loading.value = false }
}

function showDetail(row) { currentMerchant.value = row; drawerVisible.value = true }
function openReview(row) { reviewTarget.value = row; reviewForm.status = 'approved'; reviewForm.note = ''; reviewVisible.value = true }

async function submitReview() {
  await reviewMerchant(reviewTarget.value.id, reviewForm)
  ElMessage.success('审核完成')
  reviewVisible.value = false
  loadData()
}

async function doBlacklist(row) {
  await ElMessageBox.confirm(`确定将【${row.name}】加入黑名单？`, '警告', { type: 'warning' })
  await reviewMerchant(row.id, { status: 'blacklisted', note: '管理员手动拉黑' })
  ElMessage.success('已加入黑名单')
  loadData()
}

onMounted(loadData)
</script>

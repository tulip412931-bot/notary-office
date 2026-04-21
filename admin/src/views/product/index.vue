<template>
  <div class="page-card">
    <div class="page-card-title">产品管理</div>
    <div class="filter-bar">
      <el-input v-model="filters.keyword" placeholder="搜索产品/商户名称" clearable style="width:220px" @clear="loadData" @keyup.enter="loadData" />
      <el-select v-model="filters.status" placeholder="审核状态" clearable style="width:130px" @change="loadData">
        <el-option label="待审核" value="pending" /><el-option label="已通过" value="approved" /><el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="产品编号" width="90" />
      <el-table-column prop="name" label="产品名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="merchantName" label="所属商户" min-width="160" show-overflow-tooltip />
      <el-table-column prop="industry" label="行业" width="100" />
      <el-table-column label="价格" width="100"><template #default="{ row }">¥{{ row.price?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="服务期限" width="90"><template #default="{ row }">{{ row.duration }}个月</template></el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="{ pending: 'warning', approved: 'success', rejected: 'danger' }[row.status]" size="small">{{ { pending: '待审核', approved: '已通过', rejected: '已拒绝' }[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建日期" width="110" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openReview(row)" v-if="row.status==='pending'">审核</el-button>
          <el-button link type="primary" size="small" v-else disabled>已处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="reviewVisible" title="产品审核" width="480px">
      <p style="margin-bottom:8px">产品: <strong>{{ reviewTarget?.name }}</strong></p>
      <p style="margin-bottom:12px;color:#666">商户: {{ reviewTarget?.merchantName }} | 价格: ¥{{ reviewTarget?.price }}</p>
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.status">
            <el-radio value="approved">通过</el-radio><el-radio value="rejected">拒绝</el-radio>
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
import { fetchProducts, reviewProduct } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const filters = reactive({ keyword: '', status: '' })
const reviewVisible = ref(false)
const reviewTarget = ref(null)
const reviewForm = reactive({ status: 'approved', note: '' })

async function loadData() {
  loading.value = true
  try { const res = await fetchProducts(filters); tableData.value = res.list || res } finally { loading.value = false }
}

function openReview(row) { reviewTarget.value = row; reviewForm.status = 'approved'; reviewForm.note = ''; reviewVisible.value = true }

async function submitReview() {
  await reviewProduct(reviewTarget.value.id, reviewForm)
  ElMessage.success('审核完成')
  reviewVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

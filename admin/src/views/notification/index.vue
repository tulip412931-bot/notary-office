<template>
  <div class="page-card">
    <div class="page-card-title">通知中心</div>
    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column width="8">
        <template #default="{ row }"><div v-if="!row.isRead" style="width:8px;height:8px;background:#c41e3a;border-radius:50%"></div></template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200">
        <template #default="{ row }"><span :style="{ fontWeight: row.isRead ? 400 : 600 }">{{ row.title }}</span></template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
      <el-table-column label="类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="{ merchant:'', refund:'warning', risk:'danger', system:'info', complaint:'warning' }[row.type]" size="small">{{ { merchant:'商户', refund:'退款', risk:'风险', system:'系统', complaint:'投诉' }[row.type] || row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="时间" width="160" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="markRead(row)" v-if="!row.isRead">标为已读</el-button>
          <span v-else style="color:#999;font-size:12px">已读</span>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无通知消息" :image-size="80" />
      </template>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchNotifications, markNotificationRead } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

async function loadData() {
  loading.value = true
  try { const res = await fetchNotifications(); tableData.value = Array.isArray(res) ? res : [] } finally { loading.value = false }
}

async function markRead(row) {
  await markNotificationRead(row.id)
  row.isRead = true
  ElMessage.success('已标为已读')
}

onMounted(loadData)
</script>

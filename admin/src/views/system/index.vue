<template>
  <div class="page-card">
    <div class="page-card-title" style="display:flex;justify-content:space-between;align-items:center">
      <span>用户管理</span>
      <el-button type="primary" size="small" @click="openAdd">新增用户</el-button>
    </div>

    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" align="center" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="roleName" label="角色" width="120" align="center" />
      <el-table-column prop="phone" label="联系电话" width="140" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status==='active'?'success':'info'" size="small">{{ row.status==='active'?'启用':'禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLogin" label="最近登录" width="160" align="center" />
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status==='active'?'danger':'success'" size="small" @click="toggleStatus(row)">{{ row.status==='active'?'禁用':'启用' }}</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无用户数据" :image-size="80" />
      </template>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑用户':'新增用户'" width="460px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="超级管理员" value="super_admin" /><el-option label="审核员" value="auditor" /><el-option label="监管员" value="regulator" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="密码" v-if="!isEdit"><el-input v-model="form.password" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchAdminUsers, updateAdminUser } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, username: '', name: '', role: 'auditor', phone: '', password: '' })
const roleNames = { super_admin: '超级管理员', auditor: '审核员', regulator: '监管员' }

async function loadData() {
  loading.value = true
  try { const res = await fetchAdminUsers(); tableData.value = Array.isArray(res) ? res : [] } finally { loading.value = false }
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', name: '', role: 'auditor', phone: '', password: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { id: row.id, username: row.username, name: row.name, role: row.role, phone: row.phone, password: '' })
  dialogVisible.value = true
}

async function submitForm() {
  if (isEdit.value) {
    const target = tableData.value.find(u => u.id === form.id)
    if (target) { Object.assign(target, { name: form.name, role: form.role, roleName: roleNames[form.role], phone: form.phone }) }
  } else {
    tableData.value.push({ id: tableData.value.length + 1, username: form.username, name: form.name, role: form.role, roleName: roleNames[form.role], phone: form.phone, status: 'active', lastLogin: '-' })
  }
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
}

async function toggleStatus(row) {
  row.status = row.status === 'active' ? 'disabled' : 'active'
  ElMessage.success('状态已更新')
}

onMounted(loadData)
</script>

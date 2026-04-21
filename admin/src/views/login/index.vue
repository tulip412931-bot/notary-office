<template>
  <div class="login-page">
    <div class="login-bg-pattern"></div>
    <div class="login-box">
      <div class="login-header">
        <div class="emblem">&#9878;</div>
        <h2>公证处预付费资金监管平台</h2>
        <p class="login-subtitle">管理端登录</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.remember">记住账号</el-checkbox>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%;font-size:16px" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="login-footer">
        &#127963; 公证处预付费资金监管系统 v1.0
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123', remember: true })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

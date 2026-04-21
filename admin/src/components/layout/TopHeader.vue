<template>
  <div class="admin-header">
    <div class="header-left">
      <el-icon style="cursor:pointer;font-size:20px" @click="appStore.toggleSidebar"><Fold v-if="!appStore.sidebarCollapsed" /><Expand v-else /></el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" @click="router.push('/notification')">
        <el-icon style="font-size:20px"><Bell /></el-icon>
      </el-badge>
      <el-dropdown @command="handleCommand">
        <span style="cursor:pointer;display:flex;align-items:center;gap:6px;color:#303133">
          <el-avatar :size="32" style="background:var(--primary)">{{ (authStore.user.name || '管')[0] }}</el-avatar>
          <span>{{ authStore.user.name || '管理员' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { fetchNotifications } from '@/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()
const unreadCount = ref(0)

onMounted(async () => {
  try {
    const data = await fetchNotifications()
    unreadCount.value = (Array.isArray(data) ? data : []).filter(n => !n.isRead).length
  } catch { unreadCount.value = 3 }
})

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>

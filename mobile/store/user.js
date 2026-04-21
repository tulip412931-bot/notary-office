import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(JSON.parse(uni.getStorageSync('userInfo') || '{}'))
  const role = ref(uni.getStorageSync('userRole') || 'consumer')

  const isLoggedIn = computed(() => !!token.value)
  const isMerchant = computed(() => role.value === 'merchant')
  const isVerified = computed(() => !!userInfo.value.verified)

  const login = (data) => {
    token.value = data.token
    userInfo.value = data.userInfo
    role.value = data.userInfo.role || 'consumer'
    uni.setStorageSync('token', data.token)
    uni.setStorageSync('userInfo', JSON.stringify(data.userInfo))
    uni.setStorageSync('userRole', role.value)
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    role.value = 'consumer'
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('userRole')
  }

  const updateUserInfo = (info) => {
    userInfo.value = { ...userInfo.value, ...info }
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
  }

  const switchRole = (newRole) => {
    role.value = newRole
    uni.setStorageSync('userRole', newRole)
  }

  return { token, userInfo, role, isLoggedIn, isMerchant, isVerified, login, logout, updateUserInfo, switchRole }
})

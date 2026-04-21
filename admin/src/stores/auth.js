import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken, getUser, setUser } from '@/utils/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(getToken() || '')
  const user = ref(getUser() || {})

  function login(username, password) {
    token.value = 'admin_token_' + Date.now()
    user.value = {
      id: 1,
      username,
      name: username === 'admin' ? '系统管理员' : '监管员',
      role: username === 'admin' ? 'super_admin' : 'regulator',
      avatar: ''
    }
    setToken(token.value)
    setUser(user.value)
    return Promise.resolve(user.value)
  }

  function logout() {
    token.value = ''
    user.value = {}
    removeToken()
  }

  return { token, user, login, logout }
})

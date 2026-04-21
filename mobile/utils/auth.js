export const isLoggedIn = () => {
  return !!uni.getStorageSync('token')
}

export const getToken = () => {
  return uni.getStorageSync('token') || ''
}

export const setToken = (token) => {
  uni.setStorageSync('token', token)
}

export const removeToken = () => {
  uni.removeStorageSync('token')
}

export const getUserInfo = () => {
  try {
    return JSON.parse(uni.getStorageSync('userInfo') || '{}')
  } catch {
    return {}
  }
}

export const setUserInfo = (info) => {
  uni.setStorageSync('userInfo', JSON.stringify(info))
}

export const clearAuth = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

export const checkLogin = (redirectUrl) => {
  if (!isLoggedIn()) {
    uni.navigateTo({
      url: `/pages/login/index?redirect=${encodeURIComponent(redirectUrl || '')}`
    })
    return false
  }
  return true
}

export const getUserRole = () => {
  return uni.getStorageSync('userRole') || 'consumer'
}

export const setUserRole = (role) => {
  uni.setStorageSync('userRole', role)
}

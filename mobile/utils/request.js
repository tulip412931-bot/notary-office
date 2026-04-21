const BASE_URL = 'https://api.notary-platform.example.com'
const USE_MOCK = true

const request = (options) => {
  return new Promise((resolve, reject) => {
    if (USE_MOCK && options.mock) {
      setTimeout(() => {
        resolve({ code: 200, data: options.mock, msg: '操作成功' })
      }, 300 + Math.random() * 400)
      return
    }

    const token = uni.getStorageSync('token') || ''
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({ url: '/pages/login/index' })
            reject(res.data)
          } else {
            uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
            reject(res.data)
          }
        } else {
          uni.showToast({ title: '网络异常', icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        if (options.mock) {
          resolve({ code: 200, data: options.mock, msg: '操作成功' })
        } else {
          uni.showToast({ title: '网络连接失败', icon: 'none' })
          reject(err)
        }
      }
    })
  })
}

export default request

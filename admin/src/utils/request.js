import axios from 'axios'
import { getToken } from './auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

request.interceptors.request.use(config => {
  const token = getToken()
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

request.interceptors.response.use(
  res => {
    const ct = res.headers['content-type'] || ''
    if (ct.includes('text/html') || (typeof res.data === 'string' && res.data.startsWith('<!DOCTYPE'))) {
      return Promise.reject(new Error('Backend not available'))
    }
    const data = res.data
    if (data && (data.code === 200 || data.code === 0 || data.success !== undefined || data.token || Array.isArray(data) || typeof data.total !== 'undefined')) {
      return data
    }
    return data
  },
  err => Promise.reject(err)
)

export default request

import request from '../utils/request'
import {
  mockMerchants, mockOrders, mockRefunds, mockNotifications,
  mockFundRecords, mockAnnouncements, mockFAQ, mockMerchantAdmin, mockReviews
} from './mockData'

// Auth APIs
export const apiLogin = (data) => request({
  url: '/api/auth/login', method: 'POST', data,
  mock: { token: 'mock_token_' + Date.now(), userInfo: { id: 'U001', name: '张三', phone: data.phone, avatar: '', role: 'consumer', verified: true } }
})

export const apiRegister = (data) => request({
  url: '/api/auth/register', method: 'POST', data,
  mock: { token: 'mock_token_' + Date.now(), userInfo: { id: 'U002', name: data.name || '新用户', phone: data.phone, avatar: '', role: 'consumer', verified: false } }
})

export const apiMerchantLogin = (data) => request({
  url: '/api/auth/merchant-login', method: 'POST', data,
  mock: { token: 'mock_merchant_token_' + Date.now(), userInfo: { id: 'M002', name: '力美健身俱乐部', phone: data.phone, avatar: '', role: 'merchant', verified: true, merchantId: 'M002' } }
})

// Merchant APIs
export const apiGetMerchants = (params) => request({
  url: '/api/merchants', data: params,
  mock: {
    list: params?.industry ? mockMerchants.filter(m => m.industry === params.industry) : mockMerchants,
    total: mockMerchants.length
  }
})

export const apiGetMerchantDetail = (id) => request({
  url: `/api/merchants/${id}`,
  mock: mockMerchants.find(m => m.id === id) || mockMerchants[0]
})

export const apiGetMerchantReviews = (merchantId) => request({
  url: `/api/merchants/${merchantId}/reviews`,
  mock: mockReviews.filter(r => r.merchantId === merchantId)
})

// Product APIs
export const apiGetProductDetail = (id) => {
  let product = null
  for (const m of mockMerchants) {
    const p = m.products.find(p => p.id === id)
    if (p) { product = { ...p, merchantId: m.id, merchantName: m.name, notaryNo: m.notaryNo }; break }
  }
  return request({ url: `/api/products/${id}`, mock: product || mockMerchants[0].products[0] })
}

// Order APIs
export const apiGetOrders = (params) => {
  let list = [...mockOrders]
  if (params?.status && params.status !== 'all') {
    list = list.filter(o => o.status === params.status)
  }
  return request({ url: '/api/orders', data: params, mock: { list, total: list.length } })
}

export const apiGetOrderDetail = (id) => request({
  url: `/api/orders/${id}`,
  mock: mockOrders.find(o => o.id === id) || mockOrders[0]
})

export const apiCreateOrder = (data) => request({
  url: '/api/orders', method: 'POST', data,
  mock: { orderId: 'ORD' + Date.now(), contractNo: 'HT-2024-' + Date.now() }
})

export const apiPayOrder = (orderId) => request({
  url: `/api/orders/${orderId}/pay`, method: 'POST',
  mock: { success: true }
})

// Refund APIs
export const apiApplyRefund = (data) => request({
  url: '/api/refunds', method: 'POST', data,
  mock: { refundId: 'RF' + Date.now() }
})

export const apiGetRefunds = () => request({
  url: '/api/refunds', mock: { list: mockRefunds, total: mockRefunds.length }
})

// Complaint APIs
export const apiSubmitComplaint = (data) => request({
  url: '/api/complaints', method: 'POST', data,
  mock: { complaintId: 'CP' + Date.now() }
})

// Notification APIs
export const apiGetNotifications = () => request({
  url: '/api/notifications', mock: { list: mockNotifications, total: mockNotifications.length }
})

// Fund APIs
export const apiGetFundRecords = () => request({
  url: '/api/fund/records', mock: { list: mockFundRecords, total: mockFundRecords.length }
})

// Announcements
export const apiGetAnnouncements = () => request({
  url: '/api/announcements', mock: { list: mockAnnouncements }
})

// FAQ
export const apiGetFAQ = () => request({
  url: '/api/faq', mock: { list: mockFAQ }
})

// Verify (real name)
export const apiVerifyIdentity = (data) => request({
  url: '/api/auth/verify', method: 'POST', data,
  mock: { verified: true }
})

// Contract
export const apiSignContract = (data) => request({
  url: '/api/contracts/sign', method: 'POST', data,
  mock: { contractId: 'CT' + Date.now(), signTime: new Date().toISOString() }
})

// Merchant Admin APIs
export const apiGetMerchantStats = () => request({
  url: '/api/merchant-admin/stats', mock: mockMerchantAdmin.stats
})

export const apiGetVerifyRecords = () => request({
  url: '/api/merchant-admin/verify-records', mock: { list: mockMerchantAdmin.verifyRecords }
})

export const apiVerifyConsumption = (data) => request({
  url: '/api/merchant-admin/verify', method: 'POST', data,
  mock: { success: true, verifyId: 'V' + Date.now() }
})

export const apiGetSettlements = () => request({
  url: '/api/merchant-admin/settlements', mock: { list: mockMerchantAdmin.settlements }
})

export const apiGetMerchantProducts = () => {
  const merchant = mockMerchants[1]
  return request({ url: '/api/merchant-admin/products', mock: { list: merchant.products } })
}

export const apiSaveProduct = (data) => request({
  url: '/api/merchant-admin/products', method: 'POST', data,
  mock: { productId: data.id || 'P' + Date.now() }
})

export const apiDeleteProduct = (id) => request({
  url: `/api/merchant-admin/products/${id}`, method: 'DELETE',
  mock: { success: true }
})

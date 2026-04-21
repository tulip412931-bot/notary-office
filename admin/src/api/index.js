import request from '@/utils/request'
import { merchants, products, orders, fundRecords, refunds, complaints, riskAlerts, notifications, adminUsers, getDashboardStats, getStatistics, ORDER_STATUS_LABEL } from './mockData'

function mock(data) { return Promise.resolve(JSON.parse(JSON.stringify(data))) }

// --- Auth ---
export function loginApi(data) { return request.post('/auth/login', data).catch(() => mock({ token: 'mock_token_' + Date.now(), user: { id: 1, username: data.username, name: '管理员', role: 'super_admin' } })) }

// --- Dashboard ---
export function fetchDashboardStats() { return request.get('/dashboard/stats').catch(() => mock(getDashboardStats())) }
export function fetchRecentOrders() { return request.get('/dashboard/recent-orders').catch(() => mock(orders.slice(0, 10))) }
export function fetchRecentAlerts() { return request.get('/dashboard/recent-alerts').catch(() => mock(riskAlerts.filter(a => a.status !== 'resolved').slice(0, 5))) }

// --- Merchant ---
export function fetchMerchants(params) {
  return request.get('/merchants', { params }).catch(() => {
    let list = [...merchants]
    if (params?.industry) list = list.filter(m => m.industry === params.industry)
    if (params?.status) list = list.filter(m => m.status === params.status)
    if (params?.keyword) list = list.filter(m => m.name.includes(params.keyword))
    return mock({ list, total: list.length })
  })
}
export function reviewMerchant(id, data) { return request.post(`/merchants/${id}/review`, data).catch(() => { const m = merchants.find(x => x.id === id); if (m) { m.status = data.status; m.reviewNote = data.note || '' } return mock({ success: true }) }) }

// --- Product ---
export function fetchProducts(params) {
  return request.get('/products', { params }).catch(() => {
    let list = [...products]
    if (params?.status) list = list.filter(p => p.status === params.status)
    if (params?.keyword) list = list.filter(p => p.name.includes(params.keyword) || p.merchantName.includes(params.keyword))
    return mock({ list, total: list.length })
  })
}
export function reviewProduct(id, data) { return request.post(`/products/${id}/review`, data).catch(() => { const p = products.find(x => x.id === id); if (p) { p.status = data.status; p.reviewNote = data.note || '' } return mock({ success: true }) }) }

// --- Order ---
export function fetchOrders(params) {
  return request.get('/orders', { params }).catch(() => {
    let list = [...orders]
    if (params?.status) list = list.filter(o => o.status === params.status)
    if (params?.keyword) list = list.filter(o => o.id.includes(params.keyword) || o.merchantName.includes(params.keyword) || o.customerName.includes(params.keyword))
    return mock({ list, total: list.length })
  })
}
export function fetchOrderDetail(id) { return request.get(`/orders/${id}`).catch(() => mock(orders.find(o => o.id === id) || {})) }
export { ORDER_STATUS_LABEL }

// --- Fund ---
export function fetchFundOverview() {
  return request.get('/fund/overview').catch(() => mock({
    totalEscrow: merchants.reduce((s, m) => s + m.escrowBalance, 0),
    totalReleased: fundRecords.filter(f => f.type === 'release').reduce((s, f) => s + f.amount, 0),
    totalRefunded: refunds.filter(r => r.status === 'approved').reduce((s, r) => s + r.refundAmount, 0),
    pendingRelease: fundRecords.filter(f => f.status === 'pending').length
  }))
}
export function fetchFundRecords(params) {
  return request.get('/fund/records', { params }).catch(() => {
    let list = [...fundRecords]
    if (params?.type) list = list.filter(f => f.type === params.type)
    if (params?.keyword) list = list.filter(f => f.merchantName.includes(params.keyword) || f.orderId.includes(params.keyword))
    return mock({ list: list.slice(0, 50), total: list.length })
  })
}
export function releaseFund(data) { return request.post('/fund/release', data).catch(() => mock({ success: true })) }

// --- Refund ---
export function fetchRefunds(params) {
  return request.get('/refunds', { params }).catch(() => {
    let list = [...refunds]
    if (params?.status) list = list.filter(r => r.status === params.status)
    return mock({ list, total: list.length })
  })
}
export function processRefund(id, data) { return request.post(`/refunds/${id}/process`, data).catch(() => { const r = refunds.find(x => x.id === id); if (r) { r.status = data.status; r.processNote = data.note || ''; r.processedAt = new Date().toISOString().slice(0, 10) } return mock({ success: true }) }) }

// --- Complaint ---
export function fetchComplaints(params) {
  return request.get('/complaints', { params }).catch(() => {
    let list = [...complaints]
    if (params?.status) list = list.filter(c => c.status === params.status)
    return mock({ list, total: list.length })
  })
}
export function handleComplaint(id, data) { return request.post(`/complaints/${id}/handle`, data).catch(() => { const c = complaints.find(x => x.id === id); if (c) { c.status = data.status; c.handleNote = data.note || '' } return mock({ success: true }) }) }

// --- Risk ---
export function fetchRiskAlerts(params) {
  return request.get('/risk/alerts', { params }).catch(() => {
    let list = [...riskAlerts]
    if (params?.severity) list = list.filter(a => a.severity === params.severity)
    if (params?.status) list = list.filter(a => a.status === params.status)
    return mock({ list, total: list.length })
  })
}
export function handleRiskAlert(id, data) { return request.post(`/risk/alerts/${id}/handle`, data).catch(() => { const a = riskAlerts.find(x => x.id === id); if (a) { a.status = data.status; a.handleNote = data.note || '' } return mock({ success: true }) }) }

// --- Statistics ---
export function fetchStatistics(params) { return request.get('/statistics', { params }).catch(() => mock(getStatistics(params?.range))) }

// --- Notification ---
export function fetchNotifications() { return request.get('/notifications').catch(() => mock(notifications)) }
export function markNotificationRead(id) { return request.post(`/notifications/${id}/read`).catch(() => { const n = notifications.find(x => x.id === id); if (n) n.isRead = true; return mock({ success: true }) }) }

// --- System ---
export function fetchAdminUsers() { return request.get('/system/users').catch(() => mock(adminUsers)) }
export function updateAdminUser(id, data) { return request.put(`/system/users/${id}`, data).catch(() => mock({ success: true })) }

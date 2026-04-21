export const formatDate = (date, fmt = 'YYYY-MM-DD HH:mm') => {
  if (!date) return ''
  const d = new Date(date)
  const map = {
    'YYYY': d.getFullYear(),
    'MM': String(d.getMonth() + 1).padStart(2, '0'),
    'DD': String(d.getDate()).padStart(2, '0'),
    'HH': String(d.getHours()).padStart(2, '0'),
    'mm': String(d.getMinutes()).padStart(2, '0'),
    'ss': String(d.getSeconds()).padStart(2, '0')
  }
  let result = fmt
  for (const [k, v] of Object.entries(map)) {
    result = result.replace(k, v)
  }
  return result
}

export const formatMoney = (amount) => {
  if (amount === null || amount === undefined) return '0.00'
  return Number(amount).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

export const getStatusText = (status) => {
  const map = {
    'pending': '待支付',
    'paid': '已支付',
    'supervising': '监管中',
    'consuming': '消费中',
    'completed': '已完成',
    'refunding': '退款中',
    'refunded': '已退款',
    'cancelled': '已取消',
    'closed': '已关闭'
  }
  return map[status] || status
}

export const getStatusColor = (status) => {
  const map = {
    'pending': '#ef6c00',
    'paid': '#1565c0',
    'supervising': '#1a3c6e',
    'consuming': '#2e7d32',
    'completed': '#2e7d32',
    'refunding': '#ef6c00',
    'refunded': '#999999',
    'cancelled': '#999999',
    'closed': '#999999'
  }
  return map[status] || '#999999'
}

export const getStatusBadgeClass = (status) => {
  const map = {
    'pending': 'badge-warning',
    'paid': 'badge-info',
    'supervising': 'badge-info',
    'consuming': 'badge-success',
    'completed': 'badge-success',
    'refunding': 'badge-warning',
    'refunded': 'badge-gray',
    'cancelled': 'badge-gray',
    'closed': 'badge-gray'
  }
  return map[status] || 'badge-gray'
}

export const getIndustryIcon = (industry) => {
  const map = {
    '教育培训': 'book',
    '健身运动': 'sport',
    '美容美发': 'beauty',
    '养老服务': 'elder',
    '其他': 'other'
  }
  return map[industry] || 'other'
}

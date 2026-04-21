/* Mock data repository for the entire admin panel */

const INDUSTRIES = ['教育培训', '健身美容', '家政服务', '旅游出行', '餐饮服务', '汽车服务', '医疗健康', '零售百货']
const STATUS_MAP = { pending: '待审核', approved: '已通过', rejected: '已拒绝', blacklisted: '已拉黑' }

function randomId() { return 'ID' + Math.random().toString(36).substr(2, 10).toUpperCase() }
function randomAmount(min, max) { return +(min + Math.random() * (max - min)).toFixed(2) }
function randomDate(startDays, endDays) {
  const d = new Date(); d.setDate(d.getDate() - Math.floor(Math.random() * (startDays - endDays) + endDays))
  return d.toISOString().slice(0, 10)
}

// --- Merchants ---
export const merchants = [
  { id: 'M001', name: '阳光教育培训中心', industry: '教育培训', contact: '张明', phone: '13800001001', status: 'approved', escrowBalance: 285000, registeredAt: '2025-06-15', creditScore: 92, address: '北京市朝阳区建国路88号', license: '91110105MA12345X', reviewNote: '' },
  { id: 'M002', name: '悦动健身俱乐部', industry: '健身美容', contact: '李娜', phone: '13800001002', status: 'approved', escrowBalance: 156000, registeredAt: '2025-07-20', creditScore: 88, address: '上海市浦东新区陆家嘴环路100号', license: '91310115MA23456Y', reviewNote: '' },
  { id: 'M003', name: '家乐家政服务有限公司', industry: '家政服务', contact: '王芳', phone: '13800001003', status: 'approved', escrowBalance: 98000, registeredAt: '2025-08-10', creditScore: 85, address: '广州市天河区天河北路200号', license: '91440106MA34567Z', reviewNote: '' },
  { id: 'M004', name: '畅游天下旅行社', industry: '旅游出行', contact: '赵强', phone: '13800001004', status: 'pending', escrowBalance: 0, registeredAt: '2026-03-01', creditScore: 0, address: '深圳市南山区科技园南路50号', license: '91440305MA45678A', reviewNote: '' },
  { id: 'M005', name: '美味轩餐饮管理', industry: '餐饮服务', contact: '刘洋', phone: '13800001005', status: 'approved', escrowBalance: 67500, registeredAt: '2025-09-05', creditScore: 78, address: '成都市武侯区天府大道300号', license: '91510107MA56789B', reviewNote: '' },
  { id: 'M006', name: '速达汽车养护中心', industry: '汽车服务', contact: '陈刚', phone: '13800001006', status: 'rejected', escrowBalance: 0, registeredAt: '2026-02-15', creditScore: 0, address: '杭州市西湖区文三路150号', license: '91330106MA67890C', reviewNote: '营业执照信息不符' },
  { id: 'M007', name: '康宁健康管理中心', industry: '医疗健康', contact: '周丽', phone: '13800001007', status: 'approved', escrowBalance: 342000, registeredAt: '2025-05-20', creditScore: 95, address: '南京市鼓楼区中山北路400号', license: '91320106MA78901D', reviewNote: '' },
  { id: 'M008', name: '优品生活超市', industry: '零售百货', contact: '吴磊', phone: '13800001008', status: 'blacklisted', escrowBalance: 12000, registeredAt: '2025-04-10', creditScore: 30, address: '武汉市江汉区解放大道500号', license: '91420103MA89012E', reviewNote: '多次违规，消费者投诉严重' }
]

// --- Products ---
export const products = []
const productNames = {
  '教育培训': ['英语一对一年卡', '数学思维半年卡', '少儿编程季卡'],
  '健身美容': ['年度健身会员卡', '私教课程20次卡', '瑜伽季度卡'],
  '家政服务': ['家庭保洁年卡', '月嫂服务套餐', '家电深度清洗卡'],
  '旅游出行': ['国内游年度VIP', '周末游10次卡'],
  '餐饮服务': ['午餐季度套餐', '火锅储值卡'],
  '汽车服务': ['汽车保养年卡', '洗车季度卡'],
  '医疗健康': ['年度体检套餐', '中医理疗月卡', '口腔护理套餐'],
  '零售百货': ['会员储值卡', '生鲜月度配送卡']
}
let pid = 1
merchants.forEach(m => {
  const names = productNames[m.industry] || ['通用套餐']
  names.forEach(pn => {
    products.push({
      id: 'P' + String(pid++).padStart(3, '0'),
      merchantId: m.id, merchantName: m.name,
      name: pn, industry: m.industry,
      price: randomAmount(500, 15000),
      duration: [3, 6, 12][Math.floor(Math.random() * 3)],
      status: m.status === 'approved' ? (['approved', 'approved', 'pending'][Math.floor(Math.random() * 3)]) : 'pending',
      description: pn + '，优质服务保障',
      createdAt: randomDate(200, 30),
      reviewNote: ''
    })
  })
})

// --- Orders ---
const ORDER_STATUSES = ['pending_pay', 'paid', 'in_service', 'completed', 'refunding', 'refunded', 'cancelled']
const ORDER_STATUS_LABEL = { pending_pay: '待支付', paid: '已支付', in_service: '服务中', completed: '已完成', refunding: '退款中', refunded: '已退款', cancelled: '已取消' }
export { ORDER_STATUS_LABEL }

export const orders = []
for (let i = 1; i <= 35; i++) {
  const m = merchants[Math.floor(Math.random() * merchants.filter(x => x.status === 'approved').length)]
  if (!m) continue
  const mp = products.filter(p => p.merchantId === m.id)
  const p = mp.length ? mp[Math.floor(Math.random() * mp.length)] : { id: 'P000', name: '通用套餐', price: 1000 }
  const st = ORDER_STATUSES[Math.floor(Math.random() * ORDER_STATUSES.length)]
  const od = randomDate(180, 1)
  orders.push({
    id: 'ORD' + String(i).padStart(5, '0'),
    merchantId: m.id, merchantName: m.name,
    productId: p.id, productName: p.name,
    customerName: ['张三', '李四', '王五', '赵六', '孙七', '周八', '吴九', '郑十'][Math.floor(Math.random() * 8)],
    customerPhone: '138' + String(Math.floor(Math.random() * 100000000)).padStart(8, '0'),
    amount: p.price || randomAmount(500, 10000),
    escrowAmount: p.price ? +(p.price * 0.8).toFixed(2) : 0,
    status: st,
    payMethod: ['微信支付', '支付宝', '银行卡'][Math.floor(Math.random() * 3)],
    createdAt: od,
    paidAt: st !== 'pending_pay' ? od : '',
    industry: m.industry
  })
}

// --- Fund records ---
export const fundRecords = []
orders.filter(o => o.status !== 'pending_pay' && o.status !== 'cancelled').forEach((o, i) => {
  fundRecords.push({
    id: 'FR' + String(i + 1).padStart(5, '0'),
    orderId: o.id, merchantId: o.merchantId, merchantName: o.merchantName,
    type: 'escrow_in',
    amount: o.escrowAmount,
    balance: randomAmount(50000, 500000),
    status: 'completed',
    createdAt: o.paidAt || o.createdAt,
    remark: '订单托管入账'
  })
  if (o.status === 'completed') {
    fundRecords.push({
      id: 'FR' + String(fundRecords.length + 1).padStart(5, '0'),
      orderId: o.id, merchantId: o.merchantId, merchantName: o.merchantName,
      type: 'release',
      amount: o.escrowAmount,
      balance: randomAmount(50000, 500000),
      status: 'completed',
      createdAt: randomDate(30, 1),
      remark: '服务完成资金释放'
    })
  }
})

// --- Refunds ---
export const refunds = orders.filter(o => o.status === 'refunding' || o.status === 'refunded').map((o, i) => ({
  id: 'RF' + String(i + 1).padStart(4, '0'),
  orderId: o.id, merchantId: o.merchantId, merchantName: o.merchantName,
  customerName: o.customerName, customerPhone: o.customerPhone,
  amount: o.amount, refundAmount: +(o.amount * (0.5 + Math.random() * 0.5)).toFixed(2),
  reason: ['服务未开始', '服务质量不满意', '个人原因', '商家违约'][Math.floor(Math.random() * 4)],
  status: o.status === 'refunded' ? 'approved' : 'pending',
  createdAt: randomDate(60, 1),
  processedAt: o.status === 'refunded' ? randomDate(30, 1) : '',
  processNote: o.status === 'refunded' ? '审核通过，退款至原支付渠道' : ''
}))

// --- Complaints ---
export const complaints = [
  { id: 'CP001', merchantId: 'M001', merchantName: '阳光教育培训中心', customerName: '张三', phone: '13800002001', type: '服务质量', content: '购买的英语课程老师频繁更换，教学质量下降', status: 'pending', createdAt: '2026-04-10', handleNote: '' },
  { id: 'CP002', merchantId: 'M002', merchantName: '悦动健身俱乐部', customerName: '李四', phone: '13800002002', type: '退款纠纷', content: '申请退款后一个月仍未收到退款', status: 'processing', createdAt: '2026-04-05', handleNote: '已联系商户核实' },
  { id: 'CP003', merchantId: 'M005', merchantName: '美味轩餐饮管理', customerName: '王五', phone: '13800002003', type: '虚假宣传', content: '实际套餐内容与宣传严重不符', status: 'resolved', createdAt: '2026-03-20', handleNote: '已责令商户整改并赔偿消费者' },
  { id: 'CP004', merchantId: 'M008', merchantName: '优品生活超市', customerName: '赵六', phone: '13800002004', type: '商家跑路', content: '充值5000元后商家关门停业', status: 'resolved', createdAt: '2026-02-15', handleNote: '已启动资金托管赔付流程' },
  { id: 'CP005', merchantId: 'M007', merchantName: '康宁健康管理中心', customerName: '孙七', phone: '13800002005', type: '服务质量', content: '体检报告出具延迟超过两周', status: 'pending', createdAt: '2026-04-18', handleNote: '' },
  { id: 'CP006', merchantId: 'M003', merchantName: '家乐家政服务有限公司', customerName: '周八', phone: '13800002006', type: '人员资质', content: '家政人员无健康证上岗', status: 'processing', createdAt: '2026-04-12', handleNote: '已要求商户提供人员资质证明' }
]

// --- Risk alerts ---
export const riskAlerts = [
  { id: 'RA001', merchantId: 'M008', merchantName: '优品生活超市', type: '异常经营', severity: 'high', content: '近30日无新增订单，疑似停业', status: 'pending', createdAt: '2026-04-15', handleNote: '' },
  { id: 'RA002', merchantId: 'M005', merchantName: '美味轩餐饮管理', type: '投诉集中', severity: 'medium', content: '近7日投诉量激增300%', status: 'processing', createdAt: '2026-04-12', handleNote: '已通知商户整改' },
  { id: 'RA003', merchantId: 'M001', merchantName: '阳光教育培训中心', type: '退款异常', severity: 'medium', content: '退款率超过15%阈值', status: 'pending', createdAt: '2026-04-18', handleNote: '' },
  { id: 'RA004', merchantId: 'M002', merchantName: '悦动健身俱乐部', type: '资金异常', severity: 'low', content: '托管资金余额接近预警线', status: 'resolved', createdAt: '2026-04-08', handleNote: '商户已补充保证金' },
  { id: 'RA005', merchantId: 'M008', merchantName: '优品生活超市', type: '商家跑路', severity: 'high', content: '法人联系方式失效，营业场所关闭', status: 'resolved', createdAt: '2026-03-01', handleNote: '已启动托管资金保护，商户已列入黑名单' },
  { id: 'RA006', merchantId: 'M007', merchantName: '康宁健康管理中心', type: '资质过期', severity: 'low', content: '医疗经营许可证即将到期', status: 'pending', createdAt: '2026-04-20', handleNote: '' },
  { id: 'RA007', merchantId: 'M003', merchantName: '家乐家政服务有限公司', type: '合规风险', severity: 'medium', content: '部分服务人员未提交背景审查资料', status: 'processing', createdAt: '2026-04-14', handleNote: '已要求限期补充' }
]

// --- Notifications ---
export const notifications = [
  { id: 'N001', title: '新商户入驻申请', content: '畅游天下旅行社提交了入驻申请，请及时审核', type: 'merchant', isRead: false, createdAt: '2026-04-20 14:30' },
  { id: 'N002', title: '退款申请待处理', content: '有2笔退款申请等待审批', type: 'refund', isRead: false, createdAt: '2026-04-20 10:15' },
  { id: 'N003', title: '高风险预警', content: '优品生活超市触发商家跑路风险预警', type: 'risk', isRead: true, createdAt: '2026-04-19 16:45' },
  { id: 'N004', title: '月度报表已生成', content: '2026年3月份监管报表已自动生成，请查阅', type: 'system', isRead: true, createdAt: '2026-04-01 08:00' },
  { id: 'N005', title: '系统维护通知', content: '系统将于4月25日凌晨2:00-4:00进行例行维护', type: 'system', isRead: false, createdAt: '2026-04-18 09:00' },
  { id: 'N006', title: '投诉处理提醒', content: '有3条投诉超过48小时未处理', type: 'complaint', isRead: false, createdAt: '2026-04-17 11:20' },
  { id: 'N007', title: '商户信用评分更新', content: '本月商户信用评分批量更新完成', type: 'merchant', isRead: true, createdAt: '2026-04-15 15:00' }
]

// --- Admin users ---
export const adminUsers = [
  { id: 1, username: 'admin', name: '系统管理员', role: 'super_admin', roleName: '超级管理员', phone: '13900000001', status: 'active', lastLogin: '2026-04-21 08:30' },
  { id: 2, username: 'auditor1', name: '王审核', role: 'auditor', roleName: '审核员', phone: '13900000002', status: 'active', lastLogin: '2026-04-20 17:00' },
  { id: 3, username: 'regulator1', name: '张监管', role: 'regulator', roleName: '监管员', phone: '13900000003', status: 'active', lastLogin: '2026-04-21 09:15' },
  { id: 4, username: 'auditor2', name: '李审核', role: 'auditor', roleName: '审核员', phone: '13900000004', status: 'disabled', lastLogin: '2026-03-10 14:00' }
]

// --- Statistics ---
export function getStatistics(range) {
  const industryData = INDUSTRIES.map(ind => ({
    industry: ind,
    merchantCount: Math.floor(Math.random() * 20) + 5,
    orderCount: Math.floor(Math.random() * 500) + 100,
    escrowAmount: randomAmount(100000, 2000000),
    refundRate: +(Math.random() * 10).toFixed(1),
    complaintCount: Math.floor(Math.random() * 20)
  }))
  const monthlyTrend = []
  for (let i = 11; i >= 0; i--) {
    const d = new Date(); d.setMonth(d.getMonth() - i)
    monthlyTrend.push({
      month: d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'),
      orders: Math.floor(Math.random() * 300) + 100,
      amount: randomAmount(200000, 800000),
      refunds: Math.floor(Math.random() * 30) + 5
    })
  }
  return { industryData, monthlyTrend }
}

export function getDashboardStats() {
  return {
    totalMerchants: merchants.filter(m => m.status === 'approved').length,
    totalOrders: orders.length,
    escrowAmount: merchants.reduce((s, m) => s + m.escrowBalance, 0),
    pendingReviews: merchants.filter(m => m.status === 'pending').length + products.filter(p => p.status === 'pending').length,
    todayOrders: Math.floor(Math.random() * 20) + 5,
    todayAmount: randomAmount(10000, 80000),
    monthOrders: Math.floor(Math.random() * 300) + 100,
    monthAmount: randomAmount(200000, 600000)
  }
}

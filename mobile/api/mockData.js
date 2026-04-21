// Mock data - merchants
export const mockMerchants = [
  {
    id: 'M001', name: '阳光英语培训中心', industry: '教育培训', rating: 4.8, reviewCount: 326,
    verified: true, address: '朝阳区建国路88号万达广场3层', phone: '010-88886666',
    logo: '', description: '专注少儿及成人英语培训15年，拥有外教团队30余人，累计培训学员超过50000人次。已纳入公证处预付费资金监管体系，消费者权益有保障。',
    license: '京教培字[2021]第0088号', notaryNo: 'BJ-GZ-2024-001',
    supervisedAmount: 2580000, studentCount: 1200,
    products: [
      { id: 'P001', name: '少儿英语启蒙班（半年）', price: 6800, originalPrice: 8800, unit: '课程', sessions: 48, description: '适合4-6岁儿童，外教小班授课，每班不超过8人', sales: 230, image: '' },
      { id: 'P002', name: '成人商务英语精品班', price: 12800, originalPrice: 15800, unit: '课程', sessions: 60, description: '商务英语能力提升，含模拟商务场景实训', sales: 156, image: '' },
      { id: 'P003', name: '雅思冲刺班（3个月）', price: 18800, originalPrice: 22000, unit: '课程', sessions: 90, description: '针对雅思6.5-7.5分段，名师一对一辅导', sales: 89, image: '' }
    ]
  },
  {
    id: 'M002', name: '力美健身俱乐部', industry: '健身运动', rating: 4.6, reviewCount: 512,
    verified: true, address: '海淀区中关村大街1号海龙大厦B1层', phone: '010-62228888',
    logo: '', description: '高端连锁健身品牌，场馆面积3000平米，配备进口器械，提供私教、团课、游泳等多种健身服务。',
    license: '京体培字[2022]第0156号', notaryNo: 'BJ-GZ-2024-002',
    supervisedAmount: 1860000, studentCount: 860,
    products: [
      { id: 'P004', name: '年卡会员', price: 3980, originalPrice: 5980, unit: '年', sessions: 0, description: '不限次数进入健身房，含团课、游泳、桑拿', sales: 420, image: '' },
      { id: 'P005', name: '私教课程包（20节）', price: 6800, originalPrice: 8000, unit: '课程包', sessions: 20, description: '一对一私人教练指导，包含体测及个性化训练方案', sales: 186, image: '' },
      { id: 'P006', name: '瑜伽季卡', price: 2680, originalPrice: 3200, unit: '季', sessions: 36, description: '不限次数参加瑜伽团课，含哈他瑜伽、流瑜伽、阴瑜伽等', sales: 245, image: '' }
    ]
  },
  {
    id: 'M003', name: '丝域养发造型', industry: '美容美发', rating: 4.5, reviewCount: 289,
    verified: true, address: '西城区西单北大街110号老佛爷百货2层', phone: '010-66118899',
    logo: '', description: '专业美发造型品牌，提供剪发、烫染、养发护理等全方位美发服务。技师团队平均从业经验8年以上。',
    license: '京美发字[2023]第0067号', notaryNo: 'BJ-GZ-2024-003',
    supervisedAmount: 920000, studentCount: 650,
    products: [
      { id: 'P007', name: '养发护理季卡', price: 1980, originalPrice: 2800, unit: '季', sessions: 12, description: '每周一次头皮养护+造型，含高端洗护产品', sales: 312, image: '' },
      { id: 'P008', name: '烫染套餐储值卡', price: 3000, originalPrice: 3000, unit: '储值', sessions: 0, description: '储值3000元享8折优惠，可用于烫发、染发、护理等项目', sales: 198, image: '' }
    ]
  },
  {
    id: 'M004', name: '夕阳红养老服务中心', industry: '养老服务', rating: 4.9, reviewCount: 178,
    verified: true, address: '丰台区南四环西路128号', phone: '010-87776655',
    logo: '', description: '专业养老服务机构，提供日间照料、居家养老、健康管理等综合养老服务。配备专业护理团队和医疗资源。',
    license: '京民养字[2022]第0032号', notaryNo: 'BJ-GZ-2024-004',
    supervisedAmount: 3200000, studentCount: 320,
    products: [
      { id: 'P009', name: '日间照料月卡', price: 3500, originalPrice: 4000, unit: '月', sessions: 30, description: '工作日日间照料服务，含三餐、健康监测、娱乐活动', sales: 156, image: '' },
      { id: 'P010', name: '居家养老服务年卡', price: 28800, originalPrice: 36000, unit: '年', sessions: 0, description: '每周3次上门服务，含生活照料、健康管理、紧急救助', sales: 88, image: '' }
    ]
  },
  {
    id: 'M005', name: '星艺钢琴艺术中心', industry: '教育培训', rating: 4.7, reviewCount: 198,
    verified: true, address: '朝阳区望京SOHO T1栋12层', phone: '010-84669988',
    logo: '', description: '专业钢琴培训机构，拥有施坦威钢琴教室，师资均为音乐学院毕业，提供考级辅导和演出机会。',
    license: '京教培字[2023]第0211号', notaryNo: 'BJ-GZ-2024-005',
    supervisedAmount: 1580000, studentCount: 450,
    products: [
      { id: 'P011', name: '钢琴入门课程（24节）', price: 4800, originalPrice: 5800, unit: '课程', sessions: 24, description: '零基础入门，一对一教学，含教材和练习指导', sales: 167, image: '' },
      { id: 'P012', name: '考级冲刺班（12节）', price: 3600, originalPrice: 4200, unit: '课程', sessions: 12, description: '针对音协/中央院考级，名师辅导，通过率95%以上', sales: 132, image: '' }
    ]
  }
]

// Mock data - orders
export const mockOrders = [
  {
    id: 'ORD20240315001', merchantId: 'M001', merchantName: '阳光英语培训中心',
    productId: 'P001', productName: '少儿英语启蒙班（半年）',
    amount: 6800, paidAmount: 6800, refundedAmount: 0,
    consumedAmount: 2833, remainingAmount: 3967,
    consumedSessions: 20, totalSessions: 48,
    status: 'supervising', contractNo: 'HT-2024-0315-001',
    createTime: '2024-03-15 10:30:00', payTime: '2024-03-15 10:35:22',
    notaryNo: 'GZ-BJ-2024-00156'
  },
  {
    id: 'ORD20240420002', merchantId: 'M002', merchantName: '力美健身俱乐部',
    productId: 'P004', productName: '年卡会员',
    amount: 3980, paidAmount: 3980, refundedAmount: 0,
    consumedAmount: 1592, remainingAmount: 2388,
    consumedSessions: 0, totalSessions: 0,
    status: 'supervising', contractNo: 'HT-2024-0420-002',
    createTime: '2024-04-20 14:20:00', payTime: '2024-04-20 14:25:18',
    notaryNo: 'GZ-BJ-2024-00203'
  },
  {
    id: 'ORD20240501003', merchantId: 'M003', merchantName: '丝域养发造型',
    productId: 'P007', productName: '养发护理季卡',
    amount: 1980, paidAmount: 1980, refundedAmount: 0,
    consumedAmount: 1980, remainingAmount: 0,
    consumedSessions: 12, totalSessions: 12,
    status: 'completed', contractNo: 'HT-2024-0501-003',
    createTime: '2024-05-01 09:15:00', payTime: '2024-05-01 09:20:33',
    notaryNo: 'GZ-BJ-2024-00267'
  },
  {
    id: 'ORD20240610004', merchantId: 'M001', merchantName: '阳光英语培训中心',
    productId: 'P003', productName: '雅思冲刺班（3个月）',
    amount: 18800, paidAmount: 18800, refundedAmount: 12530,
    consumedAmount: 6270, remainingAmount: 0,
    consumedSessions: 30, totalSessions: 90,
    status: 'refunded', contractNo: 'HT-2024-0610-004',
    createTime: '2024-06-10 16:00:00', payTime: '2024-06-10 16:05:11',
    notaryNo: 'GZ-BJ-2024-00342'
  },
  {
    id: 'ORD20240801005', merchantId: 'M005', merchantName: '星艺钢琴艺术中心',
    productId: 'P011', productName: '钢琴入门课程（24节）',
    amount: 4800, paidAmount: 0, refundedAmount: 0,
    consumedAmount: 0, remainingAmount: 0,
    consumedSessions: 0, totalSessions: 24,
    status: 'pending', contractNo: 'HT-2024-0801-005',
    createTime: '2024-08-01 11:00:00', payTime: '',
    notaryNo: ''
  }
]

// Mock data - refunds
export const mockRefunds = [
  {
    id: 'RF20240720001', orderId: 'ORD20240610004', merchantName: '阳光英语培训中心',
    productName: '雅思冲刺班（3个月）', reason: '因工作调动，无法继续上课',
    amount: 12530, status: 'completed',
    applyTime: '2024-07-20 10:00:00', completeTime: '2024-07-25 15:30:00',
    remark: '经公证处审核，按已消费课时比例退还剩余款项'
  },
  {
    id: 'RF20240815002', orderId: 'ORD20240315001', merchantName: '阳光英语培训中心',
    productName: '少儿英语启蒙班（半年）', reason: '孩子不适应教学方式',
    amount: 3967, status: 'processing',
    applyTime: '2024-08-15 14:30:00', completeTime: '',
    remark: '审核中，公证处正在核实消费记录'
  }
]

// Mock notifications
export const mockNotifications = [
  { id: 'N001', title: '退费申请进度更新', content: '您在阳光英语培训中心的退费申请已进入公证处审核阶段，预计3个工作日内完成。', time: '2024-08-16 09:00:00', read: false, type: 'refund' },
  { id: 'N002', title: '消费核销通知', content: '力美健身俱乐部已核销您的年卡会员服务1次，消费金额¥10.88。', time: '2024-08-15 18:30:00', read: false, type: 'consume' },
  { id: 'N003', title: '资金监管报告', content: '您的预付资金月度监管报告已生成，所有资金均在公证处监管账户中安全保管。', time: '2024-08-01 10:00:00', read: true, type: 'fund' },
  { id: 'N004', title: '平台公告', content: '根据《北京市预付费资金监管办法》，自2024年9月1日起，所有新入驻商家须完成资金监管备案。', time: '2024-07-25 08:00:00', read: true, type: 'system' },
  { id: 'N005', title: '合同签署成功', content: '您与阳光英语培训中心的服务合同已通过公证处电子签章系统完成签署，合同编号：HT-2024-0315-001。', time: '2024-03-15 10:36:00', read: true, type: 'contract' }
]

// Mock fund transactions
export const mockFundRecords = [
  { id: 'F001', type: 'pay', title: '预付费支付', merchantName: '阳光英语培训中心', amount: -6800, balance: 0, time: '2024-03-15 10:35:22', orderNo: 'ORD20240315001' },
  { id: 'F002', type: 'pay', title: '预付费支付', merchantName: '力美健身俱乐部', amount: -3980, balance: 0, time: '2024-04-20 14:25:18', orderNo: 'ORD20240420002' },
  { id: 'F003', type: 'consume', title: '课程消费核销', merchantName: '阳光英语培训中心', amount: -141.67, balance: 0, time: '2024-04-01 16:00:00', orderNo: 'ORD20240315001' },
  { id: 'F004', type: 'pay', title: '预付费支付', merchantName: '丝域养发造型', amount: -1980, balance: 0, time: '2024-05-01 09:20:33', orderNo: 'ORD20240501003' },
  { id: 'F005', type: 'refund', title: '退费到账', merchantName: '阳光英语培训中心', amount: 12530, balance: 0, time: '2024-07-25 15:30:00', orderNo: 'ORD20240610004' },
  { id: 'F006', type: 'pay', title: '预付费支付', merchantName: '星艺钢琴艺术中心', amount: -18800, balance: 0, time: '2024-06-10 16:05:11', orderNo: 'ORD20240610004' }
]

// Mock announcements
export const mockAnnouncements = [
  { id: 'A001', title: '关于加强预付费资金监管的通知', summary: '为切实保障消费者预付费资金安全，现将有关事项通知如下...', time: '2024-08-10' },
  { id: 'A002', title: '新增养老服务行业预付费监管', summary: '自2024年9月起，养老服务行业纳入预付费资金监管范围...', time: '2024-07-28' },
  { id: 'A003', title: '预付费消费安全提示', summary: '提醒广大消费者，选择已纳入公证处资金监管的商家消费，保障资金安全...', time: '2024-07-15' }
]

// Mock FAQ
export const mockFAQ = [
  { q: '什么是预付费资金监管？', a: '预付费资金监管是指消费者向商家支付的预付费用，由公证处作为第三方进行资金托管和监管。商家每次提供服务后，经消费者确认，公证处才会将对应款项拨付给商家，从而保障消费者的资金安全。' },
  { q: '资金监管如何保障我的权益？', a: '您支付的预付费用将存入公证处监管账户，商家无法直接提取。每次消费后，按实际消费金额进行结算。如商家倒闭或无法提供服务，剩余资金将退还给您。' },
  { q: '如何申请退费？', a: '您可以在"我的订单"中找到需要退费的订单，点击"申请退费"，填写退费原因后提交。公证处将在3-5个工作日内审核，审核通过后资金将原路退回。' },
  { q: '商家消费核销是什么意思？', a: '商家每次为您提供服务后，需要在系统中进行消费核销，记录本次服务内容和消费金额。核销后，公证处将对应金额从监管账户拨付给商家。' },
  { q: '如何判断商家是否纳入资金监管？', a: '在本平台展示的商家均已纳入公证处预付费资金监管体系。商家详情页会显示"已纳入监管"标识和公证备案编号。' },
  { q: '预付费监管有法律依据吗？', a: '根据《消费者权益保护法》《北京市预付费资金监管办法》等法律法规，经营者应当对预付费资金实行第三方监管，保障消费者合法权益。' },
  { q: '我的个人信息安全吗？', a: '平台严格遵守《个人信息保护法》，采用银行级加密技术保护您的个人信息。实名认证信息仅用于身份核验，不会泄露给第三方。' },
  { q: '如何投诉商家？', a: '您可以通过"我的-投诉建议"或订单详情页中的"投诉"按钮提交投诉。公证处将在收到投诉后2个工作日内介入处理。' }
]

// Mock merchant admin data
export const mockMerchantAdmin = {
  stats: {
    todayOrders: 12, todayIncome: 15800, totalCustomers: 860,
    supervisedBalance: 1860000, pendingVerify: 8, monthIncome: 186000
  },
  verifyRecords: [
    { id: 'V001', customerName: '张**', productName: '年卡会员', amount: 10.88, sessions: 1, time: '2024-08-15 18:30:00', status: 'verified' },
    { id: 'V002', customerName: '李**', productName: '私教课程包', amount: 340, sessions: 1, time: '2024-08-15 16:00:00', status: 'verified' },
    { id: 'V003', customerName: '王**', productName: '瑜伽季卡', amount: 74.44, sessions: 1, time: '2024-08-15 10:30:00', status: 'verified' },
    { id: 'V004', customerName: '赵**', productName: '年卡会员', amount: 10.88, sessions: 1, time: '2024-08-14 19:00:00', status: 'verified' },
    { id: 'V005', customerName: '刘**', productName: '私教课程包', amount: 340, sessions: 1, time: '2024-08-14 15:30:00', status: 'verified' }
  ],
  settlements: [
    { id: 'S001', period: '2024年7月', amount: 168000, status: 'settled', settleTime: '2024-08-05 10:00:00', verifyCount: 856 },
    { id: 'S002', period: '2024年8月（截至今日）', amount: 86000, status: 'pending', settleTime: '', verifyCount: 423 },
    { id: 'S003', period: '2024年6月', amount: 192000, status: 'settled', settleTime: '2024-07-05 10:00:00', verifyCount: 912 }
  ]
}

// Reviews
export const mockReviews = [
  { id: 'R001', userName: '张**', rating: 5, content: '老师非常专业，孩子很喜欢上课，进步很大！资金有公证处监管，放心！', time: '2024-07-20', merchantId: 'M001' },
  { id: 'R002', userName: '李**', rating: 4, content: '环境不错，设备齐全，教练也很负责。希望能增加更多团课。', time: '2024-07-15', merchantId: 'M002' },
  { id: 'R003', userName: '王**', rating: 5, content: '服务很好，有了公证处监管，终于不用担心跑路了。', time: '2024-06-28', merchantId: 'M003' },
  { id: 'R004', userName: '陈**', rating: 5, content: '对老人照顾得很周到，工作人员态度很好，家属很放心。', time: '2024-08-01', merchantId: 'M004' },
  { id: 'R005', userName: '赵**', rating: 4, content: '钢琴老师水平很高，孩子考级一次就通过了。', time: '2024-07-10', merchantId: 'M005' }
]

import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '工作台' } },
      { path: 'merchant', name: 'Merchant', component: () => import('@/views/merchant/index.vue'), meta: { title: '商户管理' } },
      { path: 'product', name: 'Product', component: () => import('@/views/product/index.vue'), meta: { title: '产品管理' } },
      { path: 'order', name: 'Order', component: () => import('@/views/order/index.vue'), meta: { title: '订单管理' } },
      { path: 'fund', name: 'Fund', component: () => import('@/views/fund/index.vue'), meta: { title: '资金管理' } },
      { path: 'refund', name: 'Refund', component: () => import('@/views/refund/index.vue'), meta: { title: '退款管理' } },
      { path: 'complaint', name: 'Complaint', component: () => import('@/views/complaint/index.vue'), meta: { title: '投诉管理' } },
      { path: 'risk', name: 'Risk', component: () => import('@/views/risk/index.vue'), meta: { title: '风险预警' } },
      { path: 'statistics', name: 'Statistics', component: () => import('@/views/statistics/index.vue'), meta: { title: '统计报表' } },
      { path: 'notification', name: 'Notification', component: () => import('@/views/notification/index.vue'), meta: { title: '通知中心' } },
      { path: 'system', name: 'System', component: () => import('@/views/system/index.vue'), meta: { title: '系统设置' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || '管理端') + ' - 公证处预付费资金监管平台'
  const token = getToken()
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router

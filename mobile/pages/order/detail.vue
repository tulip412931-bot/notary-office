<template>
  <view class="container">
    <scroll-view scroll-y class="detail-scroll">
      <!-- Status Header -->
      <view class="status-header" :style="{ background: statusGradient }">
        <text class="status-icon">{{ statusIcon }}</text>
        <text class="status-text">{{ statusText }}</text>
        <text class="status-sub">{{ statusSub }}</text>
      </view>

      <!-- Amount Card -->
      <view class="amount-card card">
        <view class="amount-row">
          <view class="amount-item">
            <text class="amount-label">订单金额</text>
            <text class="amount-value">¥{{ formatMoney(order.amount) }}</text>
          </view>
          <view class="amount-item">
            <text class="amount-label">已消费</text>
            <text class="amount-value consumed">¥{{ formatMoney(order.consumedAmount) }}</text>
          </view>
          <view class="amount-item">
            <text class="amount-label">监管余额</text>
            <text class="amount-value remaining">¥{{ formatMoney(order.remainingAmount) }}</text>
          </view>
        </view>
        <view class="progress-section" v-if="order.totalSessions > 0">
          <view class="progress-label">
            <text>消费进度</text>
            <text>{{ order.consumedSessions }}/{{ order.totalSessions }}次</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
        </view>
      </view>

      <!-- Order Info -->
      <view class="info-card card">
        <text class="card-title">订单信息</text>
        <view class="info-item">
          <text class="info-label">订单编号</text>
          <text class="info-value" @click="copyText(order.id)">{{ order.id }} &#x1F4CB;</text>
        </view>
        <view class="info-item">
          <text class="info-label">商家名称</text>
          <text class="info-value link" @click="goMerchant">{{ order.merchantName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">服务项目</text>
          <text class="info-value">{{ order.productName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">合同编号</text>
          <text class="info-value">{{ order.contractNo }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">公证编号</text>
          <text class="info-value notary">{{ order.notaryNo || '待生成' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">下单时间</text>
          <text class="info-value">{{ order.createTime }}</text>
        </view>
        <view class="info-item" v-if="order.payTime">
          <text class="info-label">支付时间</text>
          <text class="info-value">{{ order.payTime }}</text>
        </view>
      </view>

      <!-- Timeline -->
      <view class="timeline-card card">
        <text class="card-title">状态跟踪</text>
        <view class="timeline">
          <view class="timeline-item" v-for="(item, i) in timeline" :key="i" :class="{ active: i === 0 }">
            <view class="timeline-dot"></view>
            <view class="timeline-line" v-if="i < timeline.length - 1"></view>
            <view class="timeline-content">
              <text class="timeline-title">{{ item.title }}</text>
              <text class="timeline-time text-sm text-gray">{{ item.time }}</text>
              <text class="timeline-desc text-sm" v-if="item.desc">{{ item.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Escrow Info -->
      <view class="escrow-card card">
        <view class="escrow-header">
          <text class="shield">&#x1F6E1;</text>
          <text class="escrow-title">公证监管信息</text>
        </view>
        <view class="escrow-item">
          <text class="escrow-label">监管机构</text>
          <text class="escrow-value">北京市东方公证处</text>
        </view>
        <view class="escrow-item">
          <text class="escrow-label">监管账户</text>
          <text class="escrow-value">招商银行 ****8866</text>
        </view>
        <view class="escrow-item">
          <text class="escrow-label">监管状态</text>
          <text class="escrow-value safe">资金安全</text>
        </view>
      </view>

      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <!-- Bottom Actions -->
    <view class="action-bar safe-bottom" v-if="order.status === 'supervising' || order.status === 'pending'">
      <button class="action-btn outline" @click="goComplaint" v-if="order.status === 'supervising'">投诉</button>
      <button class="action-btn outline" @click="viewContract">查看合同</button>
      <button class="action-btn primary" @click="applyRefund" v-if="order.status === 'supervising'">申请退费</button>
      <button class="action-btn accent" @click="handlePay" v-if="order.status === 'pending'">立即支付</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { apiGetOrderDetail } from '@/api/index'
import { formatMoney as fm, getStatusText } from '@/utils/format'

const order = ref({})
const formatMoney = fm

const statusText = computed(() => getStatusText(order.value.status))
const statusIcon = computed(() => {
  const map = { pending: '&#x23F3;', supervising: '&#x1F6E1;', completed: '&#x2705;', refunded: '&#x1F4B0;', refunding: '&#x23F3;' }
  return map[order.value.status] || '&#x1F4C4;'
})
const statusSub = computed(() => {
  const map = {
    pending: '请尽快完成支付',
    supervising: '资金由公证处安全监管中',
    completed: '所有服务已完成',
    refunded: '退款已到账',
    refunding: '公证处审核中'
  }
  return map[order.value.status] || ''
})
const statusGradient = computed(() => {
  const map = {
    pending: 'linear-gradient(135deg, #ef6c00, #ff9800)',
    supervising: 'linear-gradient(135deg, #1a3c6e, #2a5298)',
    completed: 'linear-gradient(135deg, #2e7d32, #43a047)',
    refunded: 'linear-gradient(135deg, #757575, #9e9e9e)',
    refunding: 'linear-gradient(135deg, #ef6c00, #ff9800)'
  }
  return map[order.value.status] || 'linear-gradient(135deg, #1a3c6e, #2a5298)'
})
const progressPercent = computed(() => {
  if (!order.value.totalSessions) return 0
  return Math.min(100, (order.value.consumedSessions / order.value.totalSessions) * 100)
})

const timeline = computed(() => {
  const items = []
  if (order.value.status === 'refunded') {
    items.push({ title: '退款完成', time: '2024-07-25 15:30:00', desc: '退款金额已原路退回' })
    items.push({ title: '退款审核通过', time: '2024-07-24 10:00:00', desc: '公证处审核通过' })
    items.push({ title: '提交退费申请', time: '2024-07-20 10:00:00' })
  }
  if (order.value.status === 'supervising' || order.value.status === 'completed') {
    if (order.value.consumedSessions > 0) {
      items.push({ title: `已完成第${order.value.consumedSessions}次服务`, time: '2024-08-10 16:00:00', desc: '商家核销，公证处已拨付对应金额' })
    }
  }
  if (order.value.payTime) {
    items.push({ title: '支付成功', time: order.value.payTime, desc: '资金已转入公证处监管账户' })
  }
  items.push({ title: '合同签署', time: order.value.createTime, desc: '电子合同经公证处签章' })
  items.push({ title: '创建订单', time: order.value.createTime })
  return items
})

const loadData = async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const id = page.$page?.options?.id || page.options?.id || 'ORD20240315001'

  try {
    const res = await apiGetOrderDetail(id)
    order.value = res.data || {}
  } catch (e) { console.error(e) }
}

const copyText = (text) => {
  uni.setClipboardData({ data: text, success: () => uni.showToast({ title: '已复制', icon: 'success' }) })
}

const goMerchant = () => {
  uni.navigateTo({ url: `/pages/merchant/detail?id=${order.value.merchantId}` })
}

const viewContract = () => {
  uni.navigateTo({
    url: `/pages/contract/sign?contractNo=${order.value.contractNo}&merchantName=${encodeURIComponent(order.value.merchantName)}&productName=${encodeURIComponent(order.value.productName)}&amount=${order.value.amount}&signed=1`
  })
}

const applyRefund = () => {
  uni.navigateTo({ url: `/pages/refund/apply?orderId=${order.value.id}` })
}

const goComplaint = () => {
  uni.navigateTo({ url: `/pages/complaint/submit?orderId=${order.value.id}&merchantName=${encodeURIComponent(order.value.merchantName)}` })
}

const handlePay = () => {
  uni.showModal({
    title: '模拟支付', content: `支付金额：¥${order.value.amount}`,
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '支付成功', icon: 'success' })
        order.value.status = 'supervising'
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.detail-scroll { height: calc(100vh - 120rpx); }
.status-header { padding: 50rpx 30rpx; text-align: center; }
.status-icon { display: block; font-size: 60rpx; margin-bottom: 12rpx; }
.status-text { display: block; font-size: 36rpx; font-weight: 700; color: #fff; margin-bottom: 8rpx; }
.status-sub { display: block; font-size: 24rpx; color: rgba(255,255,255,0.8); }

.amount-card .amount-row { display: flex; justify-content: space-around; padding: 20rpx 0; }
.amount-item { text-align: center; }
.amount-label { display: block; font-size: 24rpx; color: #999; margin-bottom: 8rpx; }
.amount-value { display: block; font-size: 32rpx; font-weight: 700; color: #333; }
.consumed { color: #ef6c00; }
.remaining { color: #1a3c6e; }

.progress-section { padding-top: 20rpx; border-top: 1rpx solid #f0f0f0; }
.progress-label { display: flex; justify-content: space-between; font-size: 24rpx; color: #666; margin-bottom: 12rpx; }
.progress-bar { height: 12rpx; background: #f0f0f0; border-radius: 6rpx; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #1a3c6e, #2a5298); border-radius: 6rpx; }

.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.info-item { display: flex; justify-content: space-between; padding: 12rpx 0; border-bottom: 1rpx solid #f8f8f8; }
.info-item:last-child { border-bottom: none; }
.info-label { color: #999; font-size: 26rpx; }
.info-value { color: #333; font-size: 26rpx; }
.link { color: #1a3c6e; }
.notary { color: #1a3c6e; font-weight: 600; }

.timeline { padding-left: 10rpx; }
.timeline-item { display: flex; position: relative; padding-bottom: 30rpx; }
.timeline-dot {
  width: 20rpx; height: 20rpx; border-radius: 50%; background: #ccc;
  margin-right: 20rpx; margin-top: 6rpx; flex-shrink: 0; z-index: 1;
}
.timeline-item.active .timeline-dot { background: #1a3c6e; box-shadow: 0 0 0 6rpx rgba(26,60,110,0.2); }
.timeline-line {
  position: absolute; left: 9rpx; top: 26rpx; bottom: 0;
  width: 2rpx; background: #e0e0e0;
}
.timeline-title { display: block; font-size: 26rpx; color: #333; font-weight: 500; }
.timeline-desc { display: block; color: #999; margin-top: 4rpx; }

.escrow-card { background: #f0f7ff !important; border: 2rpx solid #c5d4e8; }
.escrow-header { display: flex; align-items: center; margin-bottom: 16rpx; }
.shield { font-size: 28rpx; margin-right: 8rpx; }
.escrow-title { font-size: 28rpx; font-weight: 600; color: #1a3c6e; }
.escrow-item { display: flex; justify-content: space-between; padding: 10rpx 0; }
.escrow-label { color: #666; font-size: 26rpx; }
.escrow-value { color: #333; font-size: 26rpx; }
.safe { color: #2e7d32; font-weight: 600; }

.bottom-safe-lg { height: 160rpx; }
.action-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; gap: 16rpx; padding: 16rpx 30rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}
.action-btn {
  flex: 1; height: 80rpx; line-height: 80rpx; border-radius: 40rpx;
  font-size: 28rpx; text-align: center; border: none;
}
.action-btn.outline { background: #fff !important; color: #1a3c6e !important; border: 2rpx solid #1a3c6e !important; }
.action-btn.primary { background: linear-gradient(135deg, #1a3c6e, #2a5298) !important; color: #fff !important; }
.action-btn.accent { background: linear-gradient(135deg, #c41e3a, #e63950) !important; color: #fff !important; }
</style>

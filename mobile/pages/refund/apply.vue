<template>
  <view class="container">
    <scroll-view scroll-y class="form-scroll">
      <!-- Order Info -->
      <view class="order-info card">
        <text class="card-title">退费订单</text>
        <view class="info-row">
          <text class="label">商家名称</text>
          <text class="value">{{ order.merchantName }}</text>
        </view>
        <view class="info-row">
          <text class="label">服务项目</text>
          <text class="value">{{ order.productName }}</text>
        </view>
        <view class="info-row">
          <text class="label">订单金额</text>
          <text class="value">¥{{ formatMoney(order.amount) }}</text>
        </view>
        <view class="info-row">
          <text class="label">已消费金额</text>
          <text class="value consumed">¥{{ formatMoney(order.consumedAmount) }}</text>
        </view>
        <view class="info-row highlight">
          <text class="label">可退金额</text>
          <text class="value refund-amount">¥{{ formatMoney(order.remainingAmount) }}</text>
        </view>
      </view>

      <!-- Refund Reason -->
      <view class="reason-card card">
        <text class="card-title">退费原因</text>
        <view class="reason-list">
          <view class="reason-item" v-for="(r, i) in reasons" :key="i"
            :class="{ selected: selectedReason === i }" @click="selectedReason = i">
            <view class="radio" :class="{ checked: selectedReason === i }">
              <text v-if="selectedReason === i">&#x2713;</text>
            </view>
            <text>{{ r }}</text>
          </view>
        </view>
        <view class="remark-area">
          <text class="remark-label">补充说明（选填）</text>
          <textarea class="remark-input" v-model="remark" placeholder="请描述退费原因" maxlength="200" />
          <text class="char-count text-sm text-gray">{{ remark.length }}/200</text>
        </view>
      </view>

      <!-- Notice -->
      <view class="notice card">
        <text class="notice-title">&#x1F6E1; 退费说明</text>
        <text class="notice-text">1. 退费金额按订单总额减去已消费金额计算</text>
        <text class="notice-text">2. 退费申请提交后，公证处将在3-5个工作日内审核</text>
        <text class="notice-text">3. 审核通过后，资金将原路退回您的支付账户</text>
        <text class="notice-text">4. 如有争议，可联系公证处协调处理</text>
      </view>

      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <view class="submit-bar safe-bottom">
      <view class="refund-info">
        <text class="refund-label">退费金额：</text>
        <text class="refund-total">¥{{ formatMoney(order.remainingAmount) }}</text>
      </view>
      <button class="submit-btn" @click="handleSubmit">提交申请</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetOrderDetail, apiApplyRefund } from '@/api/index'
import { formatMoney as fm } from '@/utils/format'

const order = ref({})
const selectedReason = ref(-1)
const remark = ref('')
const formatMoney = fm

const reasons = [
  '商家服务质量不佳',
  '个人原因无法继续消费',
  '商家经营异常（停业/搬迁）',
  '与商家协商一致退费',
  '其他原因'
]

const loadData = async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const orderId = page.$page?.options?.orderId || page.options?.orderId || 'ORD20240315001'

  try {
    const res = await apiGetOrderDetail(orderId)
    order.value = res.data || {}
  } catch (e) { console.error(e) }
}

const handleSubmit = async () => {
  if (selectedReason.value < 0) {
    uni.showToast({ title: '请选择退费原因', icon: 'none' }); return
  }

  uni.showModal({
    title: '确认提交',
    content: `确定申请退费 ¥${fm(order.value.remainingAmount)} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        uni.showLoading({ title: '提交中...' })
        try {
          await apiApplyRefund({
            orderId: order.value.id,
            reason: reasons[selectedReason.value],
            remark: remark.value,
            amount: order.value.remainingAmount
          })
          uni.hideLoading()
          uni.showToast({ title: '申请已提交', icon: 'success' })
          setTimeout(() => { uni.navigateBack() }, 1500)
        } catch (e) {
          uni.hideLoading()
        }
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.form-scroll { height: calc(100vh - 120rpx); }
.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.info-row { display: flex; justify-content: space-between; padding: 12rpx 0; border-bottom: 1rpx solid #f8f8f8; }
.info-row:last-child { border-bottom: none; }
.label { color: #999; font-size: 26rpx; }
.value { color: #333; font-size: 26rpx; }
.consumed { color: #ef6c00; }
.highlight { background: #fff8e1; margin: 12rpx -30rpx 0; padding: 16rpx 30rpx; border-radius: 8rpx; }
.refund-amount { color: #c41e3a; font-size: 32rpx; font-weight: 700; }

.reason-item {
  display: flex; align-items: center; padding: 20rpx 0;
  border-bottom: 1rpx solid #f8f8f8; font-size: 28rpx; color: #333;
}
.reason-item:last-child { border-bottom: none; }
.reason-item.selected { color: #1a3c6e; font-weight: 500; }
.radio {
  width: 40rpx; height: 40rpx; border: 2rpx solid #ccc; border-radius: 50%;
  margin-right: 16rpx; display: flex; align-items: center; justify-content: center;
  font-size: 22rpx; color: #fff;
}
.radio.checked { background: #1a3c6e; border-color: #1a3c6e; }

.remark-area { margin-top: 20rpx; }
.remark-label { display: block; font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.remark-input {
  width: 100%; height: 160rpx; border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 16rpx; font-size: 26rpx; box-sizing: border-box;
}
.char-count { display: block; text-align: right; margin-top: 8rpx; }

.notice { background: #f0f7ff !important; }
.notice-title { display: block; font-size: 28rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 12rpx; }
.notice-text { display: block; font-size: 24rpx; color: #555; line-height: 2; }

.bottom-safe-lg { height: 160rpx; }
.submit-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; align-items: center; padding: 16rpx 30rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}
.refund-label { font-size: 26rpx; color: #666; }
.refund-total { font-size: 36rpx; font-weight: 700; color: #c41e3a; }
.submit-btn {
  flex: 1; margin-left: 30rpx;
  background: linear-gradient(135deg, #c41e3a, #e63950) !important;
  color: #fff !important; border: none !important; border-radius: 44rpx !important;
  height: 88rpx; line-height: 88rpx; font-size: 32rpx !important;
}
</style>

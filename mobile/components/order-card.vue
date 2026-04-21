<template>
  <view class="order-card" @click="$emit('click', order)">
    <view class="order-header">
      <text class="merchant-name">{{ order.merchantName }}</text>
      <view :class="statusBadgeClass">
        <text>{{ statusText }}</text>
      </view>
    </view>
    <view class="order-body">
      <view class="product-info">
        <view class="product-icon flex-center">
          <text class="icon-text">{{ order.productName ? order.productName[0] : '商' }}</text>
        </view>
        <view class="product-detail flex-1">
          <text class="product-name">{{ order.productName }}</text>
          <text class="order-no text-sm text-gray">订单号：{{ order.id }}</text>
        </view>
        <view class="amount-info">
          <text class="amount">¥{{ formatMoney(order.amount) }}</text>
        </view>
      </view>
    </view>
    <view class="order-footer" v-if="order.status === 'supervising'">
      <view class="progress-info">
        <text class="text-sm text-gray">已消费 ¥{{ formatMoney(order.consumedAmount) }}</text>
        <text class="text-sm text-primary"> / 监管中 ¥{{ formatMoney(order.remainingAmount) }}</text>
      </view>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
    </view>
    <view class="order-time">
      <text class="text-sm text-gray">{{ order.createTime }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { getStatusText, getStatusBadgeClass, formatMoney as fm } from '@/utils/format'

const props = defineProps({
  order: { type: Object, default: () => ({}) }
})

defineEmits(['click'])

const formatMoney = fm

const statusText = computed(() => getStatusText(props.order.status))
const statusBadgeClass = computed(() => getStatusBadgeClass(props.order.status))

const progressPercent = computed(() => {
  if (!props.order.amount) return 0
  return Math.min(100, (props.order.consumedAmount / props.order.amount) * 100)
})
</script>

<style lang="scss" scoped>
.order-card {
  background: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.merchant-name { font-size: 28rpx; font-weight: 600; color: #333; }
.order-body { margin-bottom: 16rpx; }
.product-info { display: flex; align-items: center; }
.product-icon {
  width: 80rpx; height: 80rpx; border-radius: 12rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  margin-right: 20rpx; flex-shrink: 0;
}
.icon-text { color: #fff; font-size: 32rpx; font-weight: 600; }
.product-detail { margin-right: 16rpx; }
.product-name { font-size: 26rpx; color: #333; display: block; margin-bottom: 8rpx; }
.order-no { display: block; }
.amount-info { text-align: right; }
.amount { font-size: 32rpx; font-weight: 600; color: #c41e3a; }
.order-footer { margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; }
.progress-info { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.progress-bar { height: 8rpx; background: #f0f0f0; border-radius: 4rpx; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #1a3c6e, #2a5298); border-radius: 4rpx; transition: width 0.3s; }
.order-time { margin-top: 16rpx; padding-top: 12rpx; border-top: 1rpx solid #f0f0f0; }
</style>

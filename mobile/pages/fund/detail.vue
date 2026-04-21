<template>
  <view class="container">
    <!-- Summary -->
    <view class="summary-header">
      <view class="summary-card">
        <text class="summary-label">累计消费</text>
        <text class="summary-amount">¥{{ totalSpent }}</text>
      </view>
      <view class="summary-card">
        <text class="summary-label">累计退款</text>
        <text class="summary-amount refund">¥{{ totalRefund }}</text>
      </view>
    </view>

    <!-- Records -->
    <scroll-view scroll-y class="list-scroll" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <view class="record-item" v-for="item in records" :key="item.id">
        <view class="record-icon" :class="'type-' + item.type">
          <text>{{ item.type === 'pay' ? '支' : item.type === 'refund' ? '退' : '消' }}</text>
        </view>
        <view class="record-info flex-1">
          <text class="record-title">{{ item.title }}</text>
          <text class="record-merchant text-sm text-gray">{{ item.merchantName }}</text>
          <text class="record-time text-sm text-gray">{{ item.time }}</text>
        </view>
        <text class="record-amount" :class="{ positive: item.amount > 0 }">
          {{ item.amount > 0 ? '+' : '' }}¥{{ formatMoney(Math.abs(item.amount)) }}
        </text>
      </view>
      <empty-state v-if="!records.length" text="暂无资金记录" />
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { apiGetFundRecords } from '@/api/index'
import { formatMoney as fm } from '@/utils/format'

const records = ref([])
const refreshing = ref(false)
const formatMoney = fm

const totalSpent = computed(() => {
  const sum = records.value.filter(r => r.type === 'pay').reduce((s, r) => s + Math.abs(r.amount), 0)
  return fm(sum)
})
const totalRefund = computed(() => {
  const sum = records.value.filter(r => r.type === 'refund').reduce((s, r) => s + r.amount, 0)
  return fm(sum)
})

const loadData = async () => {
  try {
    const res = await apiGetFundRecords()
    records.value = res.data.list || []
  } catch (e) { console.error(e) }
}

const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.summary-header {
  display: flex; gap: 20rpx; padding: 30rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
}
.summary-card {
  flex: 1; background: rgba(255,255,255,0.15); border-radius: 16rpx;
  padding: 24rpx; text-align: center;
}
.summary-label { display: block; font-size: 24rpx; color: rgba(255,255,255,0.7); margin-bottom: 8rpx; }
.summary-amount { display: block; font-size: 36rpx; font-weight: 700; color: #fff; }
.summary-amount.refund { color: #a5d6a7; }

.list-scroll { height: calc(100vh - 180px); }
.record-item {
  display: flex; align-items: center; padding: 24rpx 30rpx;
  background: #fff; border-bottom: 1rpx solid #f5f5f5;
}
.record-icon {
  width: 64rpx; height: 64rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  margin-right: 20rpx; font-size: 24rpx; font-weight: 600;
}
.type-pay { background: #e3f2fd; color: #1565c0; }
.type-refund { background: #e8f5e9; color: #2e7d32; }
.type-consume { background: #fff3e0; color: #ef6c00; }
.record-title { display: block; font-size: 28rpx; color: #333; margin-bottom: 4rpx; }
.record-amount { font-size: 30rpx; font-weight: 600; color: #333; }
.record-amount.positive { color: #2e7d32; }
.bottom-safe { height: 40rpx; }
</style>

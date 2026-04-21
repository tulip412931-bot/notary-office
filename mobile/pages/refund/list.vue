<template>
  <view class="container">
    <scroll-view scroll-y class="list-scroll" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <view class="refund-item card" v-for="item in refunds" :key="item.id">
        <view class="item-header">
          <text class="merchant-name">{{ item.merchantName }}</text>
          <view :class="item.status === 'completed' ? 'badge-success' : 'badge-warning'">
            <text>{{ item.status === 'completed' ? '退款完成' : '处理中' }}</text>
          </view>
        </view>
        <text class="product-name text-sm text-gray">{{ item.productName }}</text>
        <view class="item-info">
          <text class="refund-amount">¥{{ formatMoney(item.amount) }}</text>
          <text class="apply-time text-sm text-gray">{{ item.applyTime }}</text>
        </view>
        <view class="item-reason">
          <text class="reason-label text-sm text-gray">原因：</text>
          <text class="reason-text text-sm">{{ item.reason }}</text>
        </view>
        <view class="item-remark" v-if="item.remark">
          <text class="remark-text text-sm text-gray">{{ item.remark }}</text>
        </view>
      </view>
      <empty-state v-if="!refunds.length" text="暂无退费记录" />
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetRefunds } from '@/api/index'
import { formatMoney as fm } from '@/utils/format'

const refunds = ref([])
const refreshing = ref(false)
const formatMoney = fm

const loadData = async () => {
  try {
    const res = await apiGetRefunds()
    refunds.value = res.data.list || []
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
.list-scroll { height: 100vh; }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.merchant-name { font-size: 28rpx; font-weight: 600; color: #333; }
.product-name { display: block; margin-bottom: 12rpx; }
.item-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.refund-amount { font-size: 36rpx; font-weight: 700; color: #c41e3a; }
.item-reason { margin-top: 12rpx; padding-top: 12rpx; border-top: 1rpx solid #f5f5f5; }
.item-remark { margin-top: 8rpx; }
.bottom-safe { height: 40rpx; }
</style>

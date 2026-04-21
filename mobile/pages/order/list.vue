<template>
  <view class="container">
    <!-- Order Tabs -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: currentTab === tab.value }" v-for="tab in tabs" :key="tab.value" @click="switchTab(tab.value)">
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- Order List -->
    <scroll-view scroll-y class="order-scroll" @scrolltolower="loadMore" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <order-card v-for="o in filteredOrders" :key="o.id" :order="o" @click="goDetail(o.id)" />
      <empty-state v-if="!filteredOrders.length" text="暂无订单" sub-text="去商家列表选购服务吧" action-text="去看看" @action="goMerchants" />
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { apiGetOrders } from '@/api/index'
import { isLoggedIn } from '@/utils/auth'

const currentTab = ref('all')
const orders = ref([])
const refreshing = ref(false)

const tabs = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 'pending' },
  { label: '监管中', value: 'supervising' },
  { label: '已完成', value: 'completed' },
  { label: '已退款', value: 'refunded' }
]

const filteredOrders = computed(() => {
  if (currentTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === currentTab.value)
})

const loadData = async () => {
  if (!isLoggedIn()) return
  try {
    const res = await apiGetOrders()
    orders.value = res.data.list || []
  } catch (e) { console.error(e) }
}

const switchTab = (val) => { currentTab.value = val }
const loadMore = () => {}
const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
}
const goMerchants = () => {
  uni.switchTab({ url: '/pages/merchant/list' })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.tab-bar {
  display: flex; background: #fff; border-bottom: 1rpx solid #f0f0f0;
  padding: 0 10rpx;
}
.tab-item {
  flex: 1; text-align: center; padding: 24rpx 0; font-size: 26rpx;
  color: #666; position: relative;
}
.tab-item.active {
  color: #1a3c6e; font-weight: 600;
  &::after {
    content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
    width: 48rpx; height: 6rpx; background: #1a3c6e; border-radius: 3rpx;
  }
}
.order-scroll { height: calc(100vh - 150px); }
.bottom-safe { height: 40rpx; }
</style>

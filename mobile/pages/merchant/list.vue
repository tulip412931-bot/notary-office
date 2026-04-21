<template>
  <view class="container">
    <!-- Search -->
    <view class="search-header">
      <view class="search-input-wrap">
        <text class="search-icon">&#x1F50D;</text>
        <input class="search-input" v-model="keyword" placeholder="搜索商家名称" confirm-type="search" @confirm="doSearch" />
      </view>
    </view>

    <!-- Industry Tabs -->
    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-list">
        <view class="tab-item" :class="{ active: currentTab === '全部' }" @click="switchTab('全部')">
          <text>全部</text>
        </view>
        <view class="tab-item" :class="{ active: currentTab === cat }" v-for="cat in industries" :key="cat" @click="switchTab(cat)">
          <text>{{ cat }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- Merchant List -->
    <scroll-view scroll-y class="merchant-scroll" @scrolltolower="loadMore" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <merchant-card v-for="m in filteredMerchants" :key="m.id" :merchant="m" @click="goDetail(m.id)" />
      <empty-state v-if="!filteredMerchants.length" text="暂无符合条件的商家" sub-text="请尝试其他筛选条件" />
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { apiGetMerchants } from '@/api/index'

const keyword = ref('')
const currentTab = ref('全部')
const merchants = ref([])
const refreshing = ref(false)
const industries = ['教育培训', '健身运动', '美容美发', '养老服务', '其他']

const filteredMerchants = computed(() => {
  let list = merchants.value
  if (currentTab.value !== '全部') {
    list = list.filter(m => m.industry === currentTab.value)
  }
  if (keyword.value) {
    list = list.filter(m => m.name.includes(keyword.value))
  }
  return list
})

const loadData = async () => {
  try {
    const res = await apiGetMerchants()
    merchants.value = res.data.list || []
  } catch (e) { console.error(e) }
}

const switchTab = (tab) => { currentTab.value = tab }
const doSearch = () => {}
const loadMore = () => {}
const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/merchant/detail?id=${id}` })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.search-header { padding: 20rpx 30rpx; background: #fff; }
.search-input-wrap {
  display: flex; align-items: center; background: #f5f6fa;
  border-radius: 40rpx; padding: 16rpx 24rpx;
}
.search-icon { font-size: 28rpx; margin-right: 12rpx; }
.search-input { flex: 1; font-size: 26rpx; }
.tab-scroll { white-space: nowrap; background: #fff; border-bottom: 1rpx solid #f0f0f0; }
.tab-list { display: flex; padding: 0 20rpx; }
.tab-item {
  padding: 20rpx 28rpx; font-size: 28rpx; color: #666; position: relative; flex-shrink: 0;
}
.tab-item.active {
  color: #1a3c6e; font-weight: 600;
  &::after {
    content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
    width: 48rpx; height: 6rpx; background: #1a3c6e; border-radius: 3rpx;
  }
}
.merchant-scroll { height: calc(100vh - 240px); }
.bottom-safe { height: 40rpx; }
</style>

<template>
  <view class="container">
    <!-- Header -->
    <view class="admin-header">
      <view class="status-bar-placeholder"></view>
      <view class="header-content">
        <view class="merchant-info">
          <view class="merchant-avatar flex-center">
            <text class="avatar-text">力</text>
          </view>
          <view class="merchant-detail">
            <text class="merchant-name">力美健身俱乐部</text>
            <view class="verified-tag">
              <text>&#x2713; 已纳入监管</text>
            </view>
          </view>
        </view>
        <view class="switch-btn" @click="switchToConsumer">
          <text class="switch-text">切换消费者</text>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="admin-scroll" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <!-- Stats Grid -->
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ stats.todayOrders }}</text>
          <text class="stat-label">今日订单</text>
        </view>
        <view class="stat-item">
          <text class="stat-value accent">¥{{ formatNum(stats.todayIncome) }}</text>
          <text class="stat-label">今日收入</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.totalCustomers }}</text>
          <text class="stat-label">累计客户</text>
        </view>
        <view class="stat-item">
          <text class="stat-value primary">¥{{ formatNum(stats.supervisedBalance) }}</text>
          <text class="stat-label">监管余额</text>
        </view>
        <view class="stat-item">
          <text class="stat-value warning">{{ stats.pendingVerify }}</text>
          <text class="stat-label">待核销</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">¥{{ formatNum(stats.monthIncome) }}</text>
          <text class="stat-label">本月收入</text>
        </view>
      </view>

      <!-- Quick Menu -->
      <view class="menu-grid">
        <view class="menu-item" @click="goPage('/pages/merchant-admin/verify')">
          <view class="menu-icon-box verify-bg">
            <text class="menu-icon">&#x1F4CB;</text>
          </view>
          <text class="menu-label">核销管理</text>
          <view class="menu-badge" v-if="stats.pendingVerify">
            <text>{{ stats.pendingVerify }}</text>
          </view>
        </view>
        <view class="menu-item" @click="goPage('/pages/merchant-admin/fund')">
          <view class="menu-icon-box fund-bg">
            <text class="menu-icon">&#x1F4B0;</text>
          </view>
          <text class="menu-label">资金管理</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/merchant-admin/products')">
          <view class="menu-icon-box product-bg">
            <text class="menu-icon">&#x1F4E6;</text>
          </view>
          <text class="menu-label">商品管理</text>
        </view>
        <view class="menu-item" @click="showTip">
          <view class="menu-icon-box setting-bg">
            <text class="menu-icon">&#x2699;</text>
          </view>
          <text class="menu-label">商家设置</text>
        </view>
      </view>

      <!-- Recent Verifications -->
      <view class="section-title">最近核销</view>
      <view class="verify-list card">
        <view class="verify-item" v-for="item in recentVerify" :key="item.id">
          <view class="verify-left">
            <text class="customer-name">{{ item.customerName }}</text>
            <text class="product-name text-sm text-gray">{{ item.productName }}</text>
          </view>
          <view class="verify-right">
            <text class="verify-amount">¥{{ item.amount }}</text>
            <text class="verify-time text-sm text-gray">{{ item.time.split(' ')[1] }}</text>
          </view>
        </view>
      </view>

      <!-- Supervision Notice -->
      <view class="supervision-notice card">
        <view class="notice-row">
          <text class="shield">&#x1F6E1;</text>
          <text class="notice-title">监管提醒</text>
        </view>
        <text class="notice-content">
          请确保每次服务后及时进行消费核销，公证处将按核销记录向您拨付资金。虚假核销将被追究法律责任。
        </text>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetMerchantStats, apiGetVerifyRecords } from '@/api/index'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const refreshing = ref(false)
const stats = ref({ todayOrders: 0, todayIncome: 0, totalCustomers: 0, supervisedBalance: 0, pendingVerify: 0, monthIncome: 0 })
const recentVerify = ref([])

const formatNum = (n) => {
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return n.toLocaleString()
}

const loadData = async () => {
  try {
    const [statsRes, verifyRes] = await Promise.all([
      apiGetMerchantStats(),
      apiGetVerifyRecords()
    ])
    stats.value = statsRes.data || {}
    recentVerify.value = (verifyRes.data.list || []).slice(0, 5)
  } catch (e) { console.error(e) }
}

const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}

const goPage = (url) => { uni.navigateTo({ url }) }
const showTip = () => { uni.showToast({ title: '功能开发中', icon: 'none' }) }

const switchToConsumer = () => {
  userStore.switchRole('consumer')
  uni.switchTab({ url: '/pages/index/index' })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.admin-header {
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  padding-bottom: 30rpx;
}
.status-bar-placeholder { height: var(--status-bar-height, 44px); }
.header-content { padding: 20rpx 30rpx; display: flex; justify-content: space-between; align-items: center; }
.merchant-info { display: flex; align-items: center; }
.merchant-avatar {
  width: 80rpx; height: 80rpx; border-radius: 16rpx;
  background: rgba(255,255,255,0.2); margin-right: 16rpx;
}
.avatar-text { color: #fff; font-size: 36rpx; font-weight: 700; }
.merchant-name { display: block; font-size: 32rpx; font-weight: 700; color: #fff; }
.verified-tag {
  display: inline-block; background: rgba(46,125,50,0.3);
  padding: 2rpx 12rpx; border-radius: 12rpx; margin-top: 6rpx;
  font-size: 20rpx; color: #a5d6a7;
}
.switch-btn {
  background: rgba(255,255,255,0.2); padding: 10rpx 24rpx;
  border-radius: 30rpx;
}
.switch-text { color: #fff; font-size: 24rpx; }

.admin-scroll { height: calc(100vh - 200px); }

.stats-grid {
  display: flex; flex-wrap: wrap; background: #fff; padding: 20rpx;
  margin: 0 0 20rpx;
}
.stat-item {
  width: 33.33%; text-align: center; padding: 20rpx 0;
}
.stat-value { display: block; font-size: 36rpx; font-weight: 700; color: #333; margin-bottom: 6rpx; }
.stat-value.accent { color: #c41e3a; }
.stat-value.primary { color: #1a3c6e; }
.stat-value.warning { color: #ef6c00; }
.stat-label { display: block; font-size: 22rpx; color: #999; }

.menu-grid {
  display: flex; justify-content: space-around; background: #fff;
  padding: 30rpx 20rpx; margin-bottom: 20rpx;
}
.menu-item { display: flex; flex-direction: column; align-items: center; position: relative; }
.menu-icon-box {
  width: 96rpx; height: 96rpx; border-radius: 24rpx;
  display: flex; align-items: center; justify-content: center; margin-bottom: 12rpx;
}
.verify-bg { background: linear-gradient(135deg, #1a3c6e, #2a5298); }
.fund-bg { background: linear-gradient(135deg, #2e7d32, #43a047); }
.product-bg { background: linear-gradient(135deg, #ef6c00, #ff9800); }
.setting-bg { background: linear-gradient(135deg, #757575, #9e9e9e); }
.menu-icon { font-size: 40rpx; }
.menu-label { font-size: 24rpx; color: #333; }
.menu-badge {
  position: absolute; top: -8rpx; right: -8rpx;
  background: #c41e3a; color: #fff; font-size: 20rpx;
  min-width: 32rpx; height: 32rpx; border-radius: 16rpx;
  display: flex; align-items: center; justify-content: center;
  padding: 0 8rpx;
}

.verify-list { padding: 0 30rpx; }
.verify-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5;
}
.verify-item:last-child { border-bottom: none; }
.customer-name { display: block; font-size: 28rpx; color: #333; font-weight: 500; }
.product-name { display: block; }
.verify-amount { display: block; font-size: 28rpx; font-weight: 600; color: #2e7d32; text-align: right; }

.supervision-notice { background: #fff8e1 !important; border: 2rpx solid #ffe082; }
.notice-row { display: flex; align-items: center; margin-bottom: 12rpx; }
.shield { font-size: 28rpx; margin-right: 8rpx; }
.notice-title { font-size: 28rpx; font-weight: 600; color: #ef6c00; }
.notice-content { font-size: 24rpx; color: #666; line-height: 1.8; }
.bottom-safe { height: 40rpx; }
</style>

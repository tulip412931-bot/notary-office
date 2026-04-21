<template>
  <view class="container">
    <!-- Header Banner -->
    <view class="header-banner">
      <view class="banner-bg">
        <view class="status-bar-placeholder"></view>
        <view class="banner-content">
          <view class="banner-top">
            <view class="platform-identity">
              <text class="emblem">&#x2696;</text>
              <view class="platform-text">
                <text class="platform-name">公证预付费监管平台</text>
                <text class="platform-sub">资金安全 · 公证保障 · 消费无忧</text>
              </view>
            </view>
            <view class="header-actions">
              <view class="action-icon" @click="goNotification">
                <text class="icon-bell">&#x1F514;</text>
                <view class="badge-dot" v-if="hasUnread"></view>
              </view>
            </view>
          </view>
          <!-- Search -->
          <view class="search-bar" @click="goSearch">
            <text class="search-icon">&#x1F50D;</text>
            <text class="search-placeholder">搜索商家、服务项目</text>
          </view>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="main-scroll" @scrolltolower="loadMore" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <!-- Quick Actions -->
      <view class="quick-actions card">
        <view class="action-item" @click="handleScan">
          <view class="action-icon-box scan-bg">
            <text class="action-icon-text">&#x1F4F7;</text>
          </view>
          <text class="action-label">扫码支付</text>
        </view>
        <view class="action-item" @click="goRefund">
          <view class="action-icon-box refund-bg">
            <text class="action-icon-text">&#x1F4B0;</text>
          </view>
          <text class="action-label">退费申请</text>
        </view>
        <view class="action-item" @click="goComplaint">
          <view class="action-icon-box complaint-bg">
            <text class="action-icon-text">&#x1F4DD;</text>
          </view>
          <text class="action-label">投诉建议</text>
        </view>
        <view class="action-item" @click="goHelp">
          <view class="action-icon-box help-bg">
            <text class="action-icon-text">&#x2753;</text>
          </view>
          <text class="action-label">帮助中心</text>
        </view>
      </view>

      <!-- Safety Notice -->
      <view class="notice-bar" v-if="announcements.length">
        <text class="notice-icon">&#x1F4E2;</text>
        <swiper class="notice-swiper" vertical autoplay circular :interval="3000">
          <swiper-item v-for="item in announcements" :key="item.id">
            <text class="notice-text">{{ item.title }}</text>
          </swiper-item>
        </swiper>
      </view>

      <!-- Industry Categories -->
      <view class="section-title">行业分类</view>
      <view class="category-grid">
        <view class="category-item" v-for="cat in categories" :key="cat.name" @click="goCategory(cat.name)">
          <view class="category-icon-box" :style="{ background: cat.bgColor }">
            <text class="category-icon">{{ cat.icon }}</text>
          </view>
          <text class="category-name">{{ cat.name }}</text>
        </view>
      </view>

      <!-- Security Badge -->
      <view class="security-card card">
        <view class="security-content">
          <view class="security-left">
            <text class="shield-icon">&#x1F6E1;</text>
          </view>
          <view class="security-info flex-1">
            <text class="security-title">公证处资金监管保障</text>
            <text class="security-desc">所有预付资金均由公证处第三方监管，消费者资金安全有保障</text>
          </view>
          <view class="security-stats">
            <text class="stat-num">{{ totalSupervised }}</text>
            <text class="stat-label">监管金额(万元)</text>
          </view>
        </view>
      </view>

      <!-- Featured Merchants -->
      <view class="section-title">推荐商家</view>
      <merchant-card
        v-for="m in merchants"
        :key="m.id"
        :merchant="m"
        @click="goMerchantDetail(m.id)"
      />

      <!-- Safety Tips -->
      <view class="section-title">安全提示</view>
      <view class="tips-card card">
        <view class="tip-item" v-for="(tip, i) in safetyTips" :key="i">
          <text class="tip-num">{{ i + 1 }}</text>
          <text class="tip-text">{{ tip }}</text>
        </view>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetMerchants, apiGetAnnouncements } from '@/api/index'
import { checkLogin } from '@/utils/auth'

const merchants = ref([])
const announcements = ref([])
const refreshing = ref(false)
const hasUnread = ref(true)

const totalSupervised = ref('1,084')

const categories = [
  { name: '教育培训', icon: '📚', bgColor: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { name: '健身运动', icon: '🏋️', bgColor: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { name: '美容美发', icon: '💇', bgColor: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { name: '养老服务', icon: '🏥', bgColor: 'linear-gradient(135deg, #43e97b, #38f9d7)' },
  { name: '其他', icon: '📋', bgColor: 'linear-gradient(135deg, #fa709a, #fee140)' }
]

const safetyTips = [
  '选择已纳入公证处资金监管的商家消费，保障资金安全',
  '预付费消费前，请仔细阅读服务合同条款',
  '每次消费后，请及时确认核销记录',
  '如遇商家违约或经营异常，请及时申请退费或投诉',
  '保护个人信息安全，不要将验证码告诉他人'
]

const loadData = async () => {
  try {
    const [merchantRes, announcementRes] = await Promise.all([
      apiGetMerchants(),
      apiGetAnnouncements()
    ])
    merchants.value = merchantRes.data.list || []
    announcements.value = announcementRes.data.list || []
  } catch (e) {
    console.error(e)
  }
}

const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}

const loadMore = () => {}

const goNotification = () => {
  if (!checkLogin('/pages/notification/list')) return
  uni.navigateTo({ url: '/pages/notification/list' })
}

const goSearch = () => {
  uni.switchTab({ url: '/pages/merchant/list' })
}

const handleScan = () => {
  if (!checkLogin()) return
  uni.showToast({ title: '扫码功能开发中', icon: 'none' })
}

const goRefund = () => {
  if (!checkLogin('/pages/refund/list')) return
  uni.navigateTo({ url: '/pages/refund/list' })
}

const goComplaint = () => {
  if (!checkLogin('/pages/complaint/submit')) return
  uni.navigateTo({ url: '/pages/complaint/submit' })
}

const goHelp = () => {
  uni.navigateTo({ url: '/pages/help/index' })
}

const goCategory = (name) => {
  uni.setStorageSync('selectedIndustry', name)
  uni.switchTab({ url: '/pages/merchant/list' })
}

const goMerchantDetail = (id) => {
  uni.navigateTo({ url: `/pages/merchant/detail?id=${id}` })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.header-banner { position: relative; }
.banner-bg {
  background: linear-gradient(135deg, #1a3c6e 0%, #2a5298 60%, #1a3c6e 100%);
  padding-bottom: 30rpx;
}
.status-bar-placeholder { height: var(--status-bar-height, 44px); }
.banner-content { padding: 0 30rpx; }
.banner-top { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; }
.platform-identity { display: flex; align-items: center; }
.emblem { font-size: 48rpx; margin-right: 16rpx; }
.platform-name { display: block; font-size: 36rpx; font-weight: 700; color: #fff; }
.platform-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.8); margin-top: 4rpx; }
.header-actions { position: relative; }
.icon-bell { font-size: 40rpx; }
.badge-dot { position: absolute; top: 0; right: 0; width: 16rpx; height: 16rpx; background: #c41e3a; border-radius: 50%; border: 2rpx solid #fff; }

.search-bar {
  display: flex; align-items: center; background: rgba(255,255,255,0.2);
  border-radius: 40rpx; padding: 16rpx 24rpx; margin-top: 16rpx;
}
.search-icon { font-size: 28rpx; margin-right: 12rpx; }
.search-placeholder { color: rgba(255,255,255,0.7); font-size: 26rpx; }

.main-scroll { height: calc(100vh - 200px); }

.quick-actions {
  display: flex; justify-content: space-around; padding: 30rpx 20rpx;
  margin-top: -10rpx; position: relative; z-index: 1;
}
.action-item { display: flex; flex-direction: column; align-items: center; }
.action-icon-box {
  width: 88rpx; height: 88rpx; border-radius: 24rpx;
  display: flex; align-items: center; justify-content: center; margin-bottom: 12rpx;
}
.scan-bg { background: linear-gradient(135deg, #1a3c6e, #2a5298); }
.refund-bg { background: linear-gradient(135deg, #c41e3a, #e63950); }
.complaint-bg { background: linear-gradient(135deg, #ef6c00, #ff9800); }
.help-bg { background: linear-gradient(135deg, #2e7d32, #43a047); }
.action-icon-text { font-size: 36rpx; }
.action-label { font-size: 24rpx; color: #666; }

.notice-bar {
  display: flex; align-items: center; background: #fffbe6;
  margin: 0 30rpx; padding: 16rpx 24rpx; border-radius: 12rpx;
  border-left: 6rpx solid #ef6c00;
}
.notice-icon { font-size: 28rpx; margin-right: 12rpx; flex-shrink: 0; }
.notice-swiper { height: 40rpx; flex: 1; }
.notice-text { font-size: 24rpx; color: #ef6c00; line-height: 40rpx; }

.category-grid {
  display: flex; flex-wrap: wrap; padding: 10rpx 30rpx 20rpx; justify-content: space-between;
}
.category-item { width: 18%; display: flex; flex-direction: column; align-items: center; margin-bottom: 20rpx; }
.category-icon-box {
  width: 96rpx; height: 96rpx; border-radius: 24rpx;
  display: flex; align-items: center; justify-content: center; margin-bottom: 12rpx;
}
.category-icon { font-size: 40rpx; }
.category-name { font-size: 24rpx; color: #333; }

.security-card { background: linear-gradient(135deg, #e8edf5, #f0f4ff) !important; border: 2rpx solid #c5d4e8; }
.security-content { display: flex; align-items: center; }
.security-left { margin-right: 20rpx; }
.shield-icon { font-size: 60rpx; }
.security-title { display: block; font-size: 28rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 8rpx; }
.security-desc { display: block; font-size: 22rpx; color: #666; line-height: 1.5; }
.security-stats { text-align: center; margin-left: 20rpx; flex-shrink: 0; }
.stat-num { display: block; font-size: 36rpx; font-weight: 700; color: #c41e3a; }
.stat-label { display: block; font-size: 20rpx; color: #999; }

.tips-card { padding: 20rpx 30rpx; }
.tip-item { display: flex; align-items: flex-start; margin-bottom: 16rpx; }
.tip-item:last-child { margin-bottom: 0; }
.tip-num {
  width: 36rpx; height: 36rpx; border-radius: 50%; background: #1a3c6e;
  color: #fff; font-size: 22rpx; display: flex; align-items: center; justify-content: center;
  margin-right: 16rpx; flex-shrink: 0; margin-top: 4rpx;
}
.tip-text { font-size: 24rpx; color: #555; line-height: 1.6; flex: 1; }

.bottom-safe { height: 40rpx; }
</style>

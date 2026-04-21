<template>
  <view class="container">
    <!-- Profile Header -->
    <view class="profile-header">
      <view class="status-bar-placeholder"></view>
      <view class="header-content" @click="goLogin">
        <view class="avatar-wrap">
          <view class="avatar">
            <text class="avatar-text">{{ userInfo.name ? userInfo.name[0] : '未' }}</text>
          </view>
          <view class="verified-dot" v-if="userInfo.verified"></view>
        </view>
        <view class="user-info" v-if="isLoggedIn">
          <text class="user-name">{{ userInfo.name || '用户' }}</text>
          <text class="user-phone">{{ formatPhone(userInfo.phone) }}</text>
          <view class="verified-tag" v-if="userInfo.verified">
            <text>已实名认证</text>
          </view>
        </view>
        <view class="user-info" v-else>
          <text class="user-name">请登录</text>
          <text class="user-phone">登录后享受更多服务</text>
        </view>
        <text class="arrow">&#x276F;</text>
      </view>

      <!-- Role Switch -->
      <view class="role-switch" v-if="isLoggedIn">
        <view class="role-btn" :class="{ active: currentRole === 'consumer' }" @click="switchRole('consumer')">
          <text>消费者</text>
        </view>
        <view class="role-btn" :class="{ active: currentRole === 'merchant' }" @click="switchRole('merchant')">
          <text>商家端</text>
        </view>
      </view>
    </view>

    <!-- Menu List -->
    <scroll-view scroll-y class="menu-scroll">
      <view class="menu-group">
        <view class="menu-item" @click="goPage('/pages/auth/verify')">
          <text class="menu-icon">&#x1F4CB;</text>
          <text class="menu-label flex-1">实名认证</text>
          <view class="verified-status" v-if="userInfo.verified">
            <text class="status-done">已认证</text>
          </view>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/order/list')">
          <text class="menu-icon">&#x1F4C4;</text>
          <text class="menu-label flex-1">我的订单</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/refund/list')">
          <text class="menu-icon">&#x1F4B3;</text>
          <text class="menu-label flex-1">退费记录</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/complaint/submit')">
          <text class="menu-icon">&#x1F4E9;</text>
          <text class="menu-label flex-1">我的投诉</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @click="goPage('/pages/fund/detail')">
          <text class="menu-icon">&#x1F4B0;</text>
          <text class="menu-label flex-1">资金明细</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/notification/list')">
          <text class="menu-icon">&#x1F514;</text>
          <text class="menu-label flex-1">消息通知</text>
          <view class="unread-badge" v-if="unreadCount">
            <text>{{ unreadCount }}</text>
          </view>
          <text class="menu-arrow">&#x276F;</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @click="goPage('/pages/help/index')">
          <text class="menu-icon">&#x2753;</text>
          <text class="menu-label flex-1">帮助中心</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="showAbout">
          <text class="menu-icon">&#x2139;</text>
          <text class="menu-label flex-1">关于平台</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
        <view class="menu-item" @click="showSettings">
          <text class="menu-icon">&#x2699;</text>
          <text class="menu-label flex-1">设置</text>
          <text class="menu-arrow">&#x276F;</text>
        </view>
      </view>

      <view class="logout-section" v-if="isLoggedIn">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { formatPhone as fp } from '@/utils/format'
import { checkLogin, getUserRole, setUserRole } from '@/utils/auth'

const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const currentRole = ref(getUserRole())
const unreadCount = ref(2)

const formatPhone = fp

const goLogin = () => {
  if (isLoggedIn.value) return
  uni.navigateTo({ url: '/pages/login/index' })
}

const goPage = (url) => {
  if (!checkLogin()) return
  if (url.startsWith('/pages/order/')) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}

const switchRole = (role) => {
  currentRole.value = role
  userStore.switchRole(role)
  if (role === 'merchant') {
    uni.navigateTo({ url: '/pages/merchant-admin/index' })
  }
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/index/index' })
      }
    }
  })
}

const showAbout = () => {
  uni.showModal({
    title: '关于平台',
    content: '公证预付费监管平台 v1.0.0\n\n本平台由公证处提供预付费资金监管服务，保障消费者预付费资金安全。\n\n监管单位：北京市东方公证处\n技术支持：公证数字科技有限公司',
    showCancel: false
  })
}

const showSettings = () => {
  uni.showToast({ title: '设置页面开发中', icon: 'none' })
}

onMounted(() => {
  currentRole.value = getUserRole()
})
</script>

<style lang="scss" scoped>
.profile-header {
  background: linear-gradient(135deg, #1a3c6e 0%, #2a5298 100%);
  padding-bottom: 30rpx;
}
.status-bar-placeholder { height: var(--status-bar-height, 44px); }
.header-content {
  display: flex; align-items: center; padding: 30rpx;
}
.avatar-wrap { position: relative; margin-right: 24rpx; }
.avatar {
  width: 120rpx; height: 120rpx; border-radius: 50%;
  background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center;
  border: 4rpx solid rgba(255,255,255,0.5);
}
.avatar-text { color: #fff; font-size: 44rpx; font-weight: 700; }
.verified-dot {
  position: absolute; bottom: 4rpx; right: 4rpx;
  width: 28rpx; height: 28rpx; background: #2e7d32;
  border-radius: 50%; border: 4rpx solid #fff;
}
.user-name { display: block; font-size: 36rpx; font-weight: 700; color: #fff; }
.user-phone { display: block; font-size: 26rpx; color: rgba(255,255,255,0.7); margin-top: 6rpx; }
.verified-tag {
  display: inline-block; background: rgba(46,125,50,0.3);
  padding: 4rpx 16rpx; border-radius: 20rpx; margin-top: 8rpx;
  font-size: 22rpx; color: #a5d6a7;
}
.arrow { color: rgba(255,255,255,0.5); font-size: 28rpx; margin-left: auto; }

.role-switch {
  display: flex; margin: 0 30rpx; background: rgba(255,255,255,0.15);
  border-radius: 40rpx; padding: 4rpx;
}
.role-btn {
  flex: 1; text-align: center; padding: 14rpx 0;
  border-radius: 36rpx; font-size: 26rpx; color: rgba(255,255,255,0.7);
}
.role-btn.active {
  background: #fff; color: #1a3c6e; font-weight: 600;
}

.menu-scroll { height: calc(100vh - 350px); }
.menu-group {
  background: #fff; margin: 20rpx 0; border-radius: 0;
}
.menu-item {
  display: flex; align-items: center; padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-label { font-size: 30rpx; color: #333; }
.menu-arrow { color: #ccc; font-size: 24rpx; }
.verified-status { margin-right: 12rpx; }
.status-done { font-size: 24rpx; color: #2e7d32; }
.unread-badge {
  background: #c41e3a; color: #fff; font-size: 20rpx;
  padding: 2rpx 12rpx; border-radius: 20rpx; margin-right: 12rpx; min-width: 32rpx; text-align: center;
}

.logout-section { padding: 40rpx 30rpx; }
.logout-btn {
  background: #fff; color: #c41e3a; border: 2rpx solid #f0f0f0;
  border-radius: 12rpx; font-size: 30rpx; height: 88rpx; line-height: 88rpx;
}

.bottom-safe { height: 40rpx; }
</style>

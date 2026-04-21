<template>
  <view class="container">
    <scroll-view scroll-y class="list-scroll" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <view class="msg-item" v-for="item in notifications" :key="item.id" :class="{ unread: !item.read }" @click="readMsg(item)">
        <view class="msg-icon-box" :class="'type-' + item.type">
          <text class="msg-icon">{{ typeIcon(item.type) }}</text>
        </view>
        <view class="msg-content flex-1">
          <view class="msg-header">
            <text class="msg-title">{{ item.title }}</text>
            <view class="unread-dot" v-if="!item.read"></view>
          </view>
          <text class="msg-text">{{ item.content }}</text>
          <text class="msg-time text-sm text-gray">{{ item.time }}</text>
        </view>
      </view>
      <empty-state v-if="!notifications.length" text="暂无消息" />
      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetNotifications } from '@/api/index'

const notifications = ref([])
const refreshing = ref(false)

const typeIcon = (type) => {
  const map = { refund: '💰', consume: '📋', fund: '🛡️', system: '📢', contract: '📄' }
  return map[type] || '📩'
}

const loadData = async () => {
  try {
    const res = await apiGetNotifications()
    notifications.value = res.data.list || []
  } catch (e) { console.error(e) }
}

const onRefresh = async () => {
  refreshing.value = true
  await loadData()
  refreshing.value = false
}

const readMsg = (item) => {
  item.read = true
  uni.showModal({ title: item.title, content: item.content, showCancel: false })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.list-scroll { height: 100vh; }
.msg-item {
  display: flex; padding: 30rpx; background: #fff;
  border-bottom: 1rpx solid #f5f5f5;
}
.msg-item.unread { background: #f8faff; }
.msg-icon-box {
  width: 72rpx; height: 72rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  margin-right: 20rpx; flex-shrink: 0;
}
.type-refund { background: #fff3e0; }
.type-consume { background: #e3f2fd; }
.type-fund { background: #e8f5e9; }
.type-system { background: #fce4ec; }
.type-contract { background: #f3e5f5; }
.msg-icon { font-size: 32rpx; }
.msg-header { display: flex; align-items: center; margin-bottom: 8rpx; }
.msg-title { font-size: 28rpx; font-weight: 600; color: #333; flex: 1; }
.unread-dot { width: 16rpx; height: 16rpx; background: #c41e3a; border-radius: 50%; }
.msg-text { display: block; font-size: 24rpx; color: #666; line-height: 1.6; margin-bottom: 8rpx;
  overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
}
.bottom-safe { height: 40rpx; }
</style>

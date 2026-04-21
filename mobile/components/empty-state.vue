<template>
  <view class="empty-state flex-col flex-center">
    <view class="empty-icon">
      <text class="icon-shield">&#x26A0;</text>
    </view>
    <text class="empty-text">{{ text || '暂无数据' }}</text>
    <text class="empty-sub" v-if="subText">{{ subText }}</text>
    <view class="empty-action" v-if="actionText" @click="$emit('action')">
      <text class="action-text">{{ actionText }}</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '暂无数据' },
  subText: { type: String, default: '' },
  actionText: { type: String, default: '' }
})
defineEmits(['action'])
</script>

<style lang="scss" scoped>
.empty-state { padding: 100rpx 60rpx; }
.empty-icon { margin-bottom: 30rpx; }
.icon-shield { font-size: 80rpx; color: #ccc; }
.empty-text { font-size: 30rpx; color: #999; margin-bottom: 12rpx; }
.empty-sub { font-size: 24rpx; color: #ccc; margin-bottom: 30rpx; }
.empty-action {
  padding: 16rpx 48rpx; border: 2rpx solid #1a3c6e;
  border-radius: 40rpx; margin-top: 20rpx;
}
.action-text { color: #1a3c6e; font-size: 28rpx; }
</style>

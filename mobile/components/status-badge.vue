<template>
  <view class="status-badge" :class="badgeClass">
    <text>{{ displayText }}</text>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { getStatusText, getStatusBadgeClass } from '@/utils/format'

const props = defineProps({
  status: { type: String, default: '' },
  text: { type: String, default: '' },
  type: { type: String, default: '' }
})

const badgeClass = computed(() => {
  if (props.type) return `badge-${props.type}`
  return getStatusBadgeClass(props.status)
})

const displayText = computed(() => {
  return props.text || getStatusText(props.status)
})
</script>

<style lang="scss" scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  font-weight: 500;
}
</style>

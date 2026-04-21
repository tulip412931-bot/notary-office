<template>
  <view class="merchant-card" @click="$emit('click', merchant)">
    <view class="card-content">
      <view class="merchant-logo flex-center">
        <text class="logo-text">{{ merchant.name ? merchant.name[0] : '商' }}</text>
      </view>
      <view class="merchant-info flex-1">
        <view class="name-row">
          <text class="merchant-name">{{ merchant.name }}</text>
          <view class="verified-badge" v-if="merchant.verified">
            <text class="verified-icon">&#x2713;</text>
            <text class="verified-text">已监管</text>
          </view>
        </view>
        <view class="industry-tag">
          <text>{{ merchant.industry }}</text>
        </view>
        <view class="rating-row">
          <text class="rating-stars">{{ '★'.repeat(Math.floor(merchant.rating || 0)) }}{{ (merchant.rating % 1 >= 0.5) ? '☆' : '' }}</text>
          <text class="rating-score">{{ merchant.rating }}</text>
          <text class="review-count text-gray text-sm">{{ merchant.reviewCount }}条评价</text>
        </view>
        <text class="address text-sm text-gray" v-if="merchant.address">{{ merchant.address }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  merchant: { type: Object, default: () => ({}) }
})
defineEmits(['click'])
</script>

<style lang="scss" scoped>
.merchant-card {
  background: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}
.card-content { display: flex; align-items: flex-start; }
.merchant-logo {
  width: 100rpx; height: 100rpx; border-radius: 16rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  margin-right: 24rpx; flex-shrink: 0;
}
.logo-text { color: #fff; font-size: 40rpx; font-weight: 700; }
.name-row { display: flex; align-items: center; margin-bottom: 8rpx; flex-wrap: wrap; }
.merchant-name { font-size: 30rpx; font-weight: 600; color: #333; margin-right: 12rpx; }
.verified-badge {
  display: inline-flex; align-items: center;
  background: #e8f5e9; padding: 2rpx 12rpx; border-radius: 6rpx;
}
.verified-icon { color: #2e7d32; font-size: 20rpx; margin-right: 4rpx; }
.verified-text { color: #2e7d32; font-size: 20rpx; }
.industry-tag {
  display: inline-block; background: #e3f2fd; color: #1565c0;
  padding: 2rpx 14rpx; border-radius: 6rpx; font-size: 22rpx; margin-bottom: 8rpx;
}
.rating-row { display: flex; align-items: center; margin-bottom: 6rpx; }
.rating-stars { color: #ff9800; font-size: 24rpx; margin-right: 8rpx; }
.rating-score { color: #ff9800; font-size: 24rpx; font-weight: 600; margin-right: 12rpx; }
.address { display: block; line-height: 1.4; }
</style>

<template>
  <view class="container">
    <scroll-view scroll-y class="detail-scroll">
      <!-- Product Image Area -->
      <view class="product-banner">
        <view class="banner-placeholder flex-center">
          <text class="banner-icon">{{ product.name ? product.name[0] : '商' }}</text>
          <text class="banner-label">{{ product.name }}</text>
        </view>
      </view>

      <!-- Price & Info -->
      <view class="price-section">
        <view class="price-row">
          <text class="current-price">¥{{ product.price }}</text>
          <text class="original-price" v-if="product.originalPrice > product.price">¥{{ product.originalPrice }}</text>
          <view class="supervised-tag">
            <text>&#x1F6E1; 公证监管</text>
          </view>
        </view>
        <text class="product-title">{{ product.name }}</text>
        <text class="sales-text text-sm text-gray">已售{{ product.sales }}件</text>
      </view>

      <!-- Safety Notice -->
      <view class="safety-notice card">
        <view class="notice-header">
          <text class="notice-icon">&#x1F6E1;</text>
          <text class="notice-title">资金安全保障</text>
        </view>
        <view class="notice-items">
          <view class="notice-item">
            <text class="check-icon">&#x2713;</text>
            <text>预付资金由公证处第三方监管</text>
          </view>
          <view class="notice-item">
            <text class="check-icon">&#x2713;</text>
            <text>按消费进度分次拨付给商家</text>
          </view>
          <view class="notice-item">
            <text class="check-icon">&#x2713;</text>
            <text>未消费部分随时可申请退款</text>
          </view>
          <view class="notice-item">
            <text class="check-icon">&#x2713;</text>
            <text>电子合同经公证处签章认证</text>
          </view>
        </view>
      </view>

      <!-- Product Detail -->
      <view class="detail-section card">
        <text class="section-head">商品详情</text>
        <view class="detail-item">
          <text class="detail-label">所属商家</text>
          <text class="detail-value link-text" @click="goMerchant">{{ product.merchantName }}</text>
        </view>
        <view class="detail-item" v-if="product.sessions">
          <text class="detail-label">服务次数</text>
          <text class="detail-value">{{ product.sessions }}次</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">公证备案</text>
          <text class="detail-value notary">{{ product.notaryNo }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">商品描述</text>
          <text class="detail-value">{{ product.description }}</text>
        </view>
      </view>

      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <!-- Bottom Buy Bar -->
    <view class="buy-bar safe-bottom">
      <view class="bar-left">
        <view class="bar-action" @click="goMerchant">
          <text class="bar-icon">&#x1F3E0;</text>
          <text class="bar-label">商家</text>
        </view>
        <view class="bar-action" @click="callPhone">
          <text class="bar-icon">&#x1F4DE;</text>
          <text class="bar-label">咨询</text>
        </view>
      </view>
      <button class="buy-button" @click="handleBuy">立即购买</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetProductDetail } from '@/api/index'
import { checkLogin } from '@/utils/auth'

const product = ref({})

const loadData = async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const id = page.$page?.options?.id || page.options?.id || 'P001'

  try {
    const res = await apiGetProductDetail(id)
    product.value = res.data || {}
  } catch (e) { console.error(e) }
}

const handleBuy = () => {
  if (!checkLogin()) return
  uni.navigateTo({
    url: `/pages/order/confirm?productId=${product.value.id}&merchantId=${product.value.merchantId}`
  })
}

const goMerchant = () => {
  if (product.value.merchantId) {
    uni.navigateTo({ url: `/pages/merchant/detail?id=${product.value.merchantId}` })
  }
}

const callPhone = () => {
  uni.makePhoneCall({ phoneNumber: '400-888-9999' })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.detail-scroll { height: calc(100vh - 120rpx); }
.product-banner {
  height: 400rpx; background: linear-gradient(135deg, #e8edf5, #c5d4e8);
}
.banner-placeholder {
  height: 100%; flex-direction: column;
}
.banner-icon { font-size: 80rpx; color: #1a3c6e; margin-bottom: 16rpx; }
.banner-label { font-size: 30rpx; color: #1a3c6e; font-weight: 600; }

.price-section {
  background: #fff; padding: 30rpx;
}
.price-row { display: flex; align-items: center; margin-bottom: 12rpx; }
.current-price { font-size: 44rpx; font-weight: 700; color: #c41e3a; }
.original-price { font-size: 26rpx; color: #999; text-decoration: line-through; margin-left: 16rpx; }
.supervised-tag {
  margin-left: auto; background: #e8f5e9; color: #2e7d32;
  padding: 6rpx 16rpx; border-radius: 8rpx; font-size: 22rpx;
}
.product-title { display: block; font-size: 32rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.sales-text { display: block; }

.safety-notice { background: #f0f7ff !important; border: 2rpx solid #c5d4e8; }
.notice-header { display: flex; align-items: center; margin-bottom: 16rpx; }
.notice-icon { font-size: 32rpx; margin-right: 12rpx; }
.notice-title { font-size: 28rpx; font-weight: 600; color: #1a3c6e; }
.notice-item { display: flex; align-items: center; margin-bottom: 12rpx; font-size: 24rpx; color: #555; }
.check-icon { color: #2e7d32; margin-right: 12rpx; font-weight: 700; }

.section-head { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.detail-item { display: flex; margin-bottom: 16rpx; }
.detail-label { width: 160rpx; color: #999; font-size: 26rpx; flex-shrink: 0; }
.detail-value { color: #333; font-size: 26rpx; flex: 1; }
.link-text { color: #1a3c6e; }
.notary { color: #1a3c6e; font-weight: 600; }

.bottom-safe-lg { height: 160rpx; }

.buy-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; display: flex; align-items: center;
  padding: 16rpx 30rpx; box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}
.bar-left { display: flex; gap: 30rpx; margin-right: 30rpx; }
.bar-action { display: flex; flex-direction: column; align-items: center; }
.bar-icon { font-size: 36rpx; }
.bar-label { font-size: 20rpx; color: #666; }
.buy-button {
  flex: 1; background: linear-gradient(135deg, #1a3c6e, #2a5298) !important;
  color: #fff !important; border: none !important; border-radius: 44rpx !important;
  height: 88rpx; line-height: 88rpx; font-size: 32rpx !important; font-weight: 600;
}
</style>

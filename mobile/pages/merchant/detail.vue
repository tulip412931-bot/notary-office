<template>
  <view class="container">
    <scroll-view scroll-y class="detail-scroll">
      <!-- Merchant Header -->
      <view class="merchant-header">
        <view class="merchant-logo flex-center">
          <text class="logo-text">{{ merchant.name ? merchant.name[0] : '' }}</text>
        </view>
        <view class="merchant-basic">
          <text class="merchant-name">{{ merchant.name }}</text>
          <view class="tags-row">
            <view class="tag-verified" v-if="merchant.verified">
              <text>&#x2713; 已纳入监管</text>
            </view>
            <view class="tag-industry">
              <text>{{ merchant.industry }}</text>
            </view>
          </view>
          <view class="rating-row">
            <text class="stars">{{ '★'.repeat(Math.floor(merchant.rating || 0)) }}</text>
            <text class="score">{{ merchant.rating }}</text>
            <text class="count text-gray text-sm">{{ merchant.reviewCount }}条评价</text>
          </view>
        </view>
      </view>

      <!-- Info Section -->
      <view class="info-card card">
        <view class="info-item">
          <text class="info-label">公证备案号</text>
          <text class="info-value notary-no">{{ merchant.notaryNo }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">经营许可</text>
          <text class="info-value">{{ merchant.license }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">监管资金</text>
          <text class="info-value amount">¥{{ formatMoney(merchant.supervisedAmount) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">地址</text>
          <text class="info-value">{{ merchant.address }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">电话</text>
          <text class="info-value phone" @click="callPhone">{{ merchant.phone }}</text>
        </view>
      </view>

      <!-- Description -->
      <view class="card">
        <text class="card-title">商家简介</text>
        <text class="desc-text">{{ merchant.description }}</text>
      </view>

      <!-- Tabs -->
      <view class="detail-tabs">
        <view class="tab" :class="{ active: activeTab === 'products' }" @click="activeTab = 'products'">
          <text>服务项目</text>
        </view>
        <view class="tab" :class="{ active: activeTab === 'reviews' }" @click="activeTab = 'reviews'">
          <text>用户评价</text>
        </view>
      </view>

      <!-- Products -->
      <view v-if="activeTab === 'products'">
        <view class="product-item card" v-for="p in merchant.products" :key="p.id" @click="goProduct(p.id)">
          <view class="product-top">
            <view class="product-icon flex-center">
              <text class="p-icon-text">{{ p.name[0] }}</text>
            </view>
            <view class="product-info flex-1">
              <text class="product-name">{{ p.name }}</text>
              <text class="product-desc text-sm text-gray">{{ p.description }}</text>
            </view>
          </view>
          <view class="product-bottom">
            <view class="price-area">
              <text class="price">¥{{ p.price }}</text>
              <text class="original-price" v-if="p.originalPrice > p.price">¥{{ p.originalPrice }}</text>
            </view>
            <view class="sales-info">
              <text class="text-sm text-gray">已售{{ p.sales }}件</text>
            </view>
            <button class="buy-btn" size="mini" @click.stop="goProduct(p.id)">立即购买</button>
          </view>
        </view>
      </view>

      <!-- Reviews -->
      <view v-if="activeTab === 'reviews'">
        <view class="review-item card" v-for="r in reviews" :key="r.id">
          <view class="review-top">
            <text class="reviewer">{{ r.userName }}</text>
            <text class="review-stars">{{ '★'.repeat(r.rating) }}</text>
          </view>
          <text class="review-content">{{ r.content }}</text>
          <text class="review-time text-sm text-gray">{{ r.time }}</text>
        </view>
        <empty-state v-if="!reviews.length" text="暂无评价" />
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetMerchantDetail, apiGetMerchantReviews } from '@/api/index'
import { formatMoney as fm } from '@/utils/format'

const merchant = ref({})
const reviews = ref([])
const activeTab = ref('products')

const formatMoney = fm

const loadData = async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const id = page.$page?.options?.id || page.options?.id || 'M001'

  try {
    const [mRes, rRes] = await Promise.all([
      apiGetMerchantDetail(id),
      apiGetMerchantReviews(id)
    ])
    merchant.value = mRes.data || {}
    reviews.value = rRes.data || []
  } catch (e) { console.error(e) }
}

const goProduct = (id) => {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

const callPhone = () => {
  if (merchant.value.phone) {
    uni.makePhoneCall({ phoneNumber: merchant.value.phone })
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.detail-scroll { height: 100vh; }
.merchant-header {
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  padding: 40rpx 30rpx; display: flex; align-items: center;
}
.merchant-logo {
  width: 120rpx; height: 120rpx; border-radius: 20rpx;
  background: rgba(255,255,255,0.2); margin-right: 24rpx; flex-shrink: 0;
}
.logo-text { color: #fff; font-size: 48rpx; font-weight: 700; }
.merchant-name { display: block; font-size: 36rpx; font-weight: 700; color: #fff; margin-bottom: 10rpx; }
.tags-row { display: flex; gap: 12rpx; margin-bottom: 10rpx; flex-wrap: wrap; }
.tag-verified {
  background: rgba(46,125,50,0.3); color: #a5d6a7; font-size: 22rpx;
  padding: 4rpx 14rpx; border-radius: 6rpx;
}
.tag-industry {
  background: rgba(255,255,255,0.2); color: rgba(255,255,255,0.9); font-size: 22rpx;
  padding: 4rpx 14rpx; border-radius: 6rpx;
}
.stars { color: #ffd54f; font-size: 24rpx; }
.score { color: #ffd54f; font-size: 24rpx; margin: 0 8rpx; }
.count { color: rgba(255,255,255,0.6) !important; }

.info-card .info-item {
  display: flex; justify-content: space-between; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-card .info-item:last-child { border-bottom: none; }
.info-label { color: #999; font-size: 26rpx; }
.info-value { color: #333; font-size: 26rpx; text-align: right; flex: 1; margin-left: 20rpx; }
.notary-no { color: #1a3c6e; font-weight: 600; }
.amount { color: #c41e3a; font-weight: 600; }
.phone { color: #1a3c6e; }

.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 16rpx; }
.desc-text { font-size: 26rpx; color: #555; line-height: 1.8; }

.detail-tabs {
  display: flex; background: #fff; border-bottom: 1rpx solid #f0f0f0; margin-top: 20rpx;
}
.tab {
  flex: 1; text-align: center; padding: 24rpx 0; font-size: 28rpx;
  color: #666; position: relative;
}
.tab.active {
  color: #1a3c6e; font-weight: 600;
  &::after {
    content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
    width: 60rpx; height: 6rpx; background: #1a3c6e; border-radius: 3rpx;
  }
}

.product-item { margin: 16rpx 30rpx; }
.product-top { display: flex; margin-bottom: 16rpx; }
.product-icon {
  width: 80rpx; height: 80rpx; border-radius: 12rpx;
  background: linear-gradient(135deg, #e3f2fd, #bbdefb); margin-right: 20rpx; flex-shrink: 0;
}
.p-icon-text { color: #1a3c6e; font-size: 32rpx; font-weight: 600; }
.product-name { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 6rpx; }
.product-desc { line-height: 1.5; }
.product-bottom { display: flex; align-items: center; justify-content: space-between; }
.price { font-size: 32rpx; font-weight: 700; color: #c41e3a; }
.original-price { font-size: 22rpx; color: #999; text-decoration: line-through; margin-left: 8rpx; }
.buy-btn {
  background: linear-gradient(135deg, #1a3c6e, #2a5298) !important;
  color: #fff !important; font-size: 24rpx !important; border: none !important;
  border-radius: 30rpx !important; padding: 8rpx 24rpx !important;
}

.review-item { margin: 16rpx 30rpx; }
.review-top { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.reviewer { font-size: 26rpx; color: #333; font-weight: 500; }
.review-stars { color: #ff9800; font-size: 24rpx; }
.review-content { font-size: 26rpx; color: #555; line-height: 1.6; margin-bottom: 8rpx; }

.bottom-safe { height: 40rpx; }
</style>

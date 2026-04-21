<template>
  <view class="container">
    <scroll-view scroll-y class="confirm-scroll">
      <!-- Merchant Info -->
      <view class="merchant-bar card">
        <view class="merchant-icon flex-center">
          <text class="m-icon">{{ product.merchantName ? product.merchantName[0] : '商' }}</text>
        </view>
        <text class="merchant-name">{{ product.merchantName }}</text>
        <view class="verified-tag">
          <text>已监管</text>
        </view>
      </view>

      <!-- Product Info -->
      <view class="product-card card">
        <text class="card-title">服务项目</text>
        <view class="product-row">
          <view class="product-thumb flex-center">
            <text>{{ product.name ? product.name[0] : '' }}</text>
          </view>
          <view class="product-info flex-1">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-desc text-sm text-gray">{{ product.description }}</text>
          </view>
        </view>
        <view class="price-row">
          <text class="label">商品金额</text>
          <text class="price">¥{{ product.price }}</text>
        </view>
        <view class="price-row" v-if="product.sessions">
          <text class="label">服务次数</text>
          <text class="value">{{ product.sessions }}次</text>
        </view>
      </view>

      <!-- Contract Preview -->
      <view class="contract-card card">
        <text class="card-title">服务合同</text>
        <text class="contract-desc text-sm text-gray">购买前请阅读并签署服务合同，合同经公证处电子签章认证</text>
        <view class="contract-check" @click="contractAgreed = !contractAgreed">
          <view class="check-box" :class="{ checked: contractAgreed }">
            <text v-if="contractAgreed">&#x2713;</text>
          </view>
          <text class="check-text">我已阅读并同意</text>
          <text class="contract-link" @click.stop="previewContract">《预付费资金监管服务合同》</text>
        </view>
      </view>

      <!-- Fund Notice -->
      <view class="fund-notice card">
        <view class="notice-header">
          <text class="shield">&#x1F6E1;</text>
          <text class="notice-title">资金监管说明</text>
        </view>
        <text class="notice-text">
          您支付的费用将存入公证处监管账户，商家每次提供服务后进行核销，公证处按核销金额向商家拨付。未消费部分随时可申请退款。
        </text>
      </view>

      <!-- Payment Summary -->
      <view class="summary-card card">
        <view class="summary-row">
          <text class="label">商品金额</text>
          <text class="value">¥{{ product.price }}</text>
        </view>
        <view class="summary-row" v-if="product.originalPrice > product.price">
          <text class="label">优惠金额</text>
          <text class="value discount">-¥{{ product.originalPrice - product.price }}</text>
        </view>
        <view class="summary-divider"></view>
        <view class="summary-row total">
          <text class="label">实付金额</text>
          <text class="value total-price">¥{{ product.price }}</text>
        </view>
      </view>

      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <!-- Bottom Bar -->
    <view class="pay-bar safe-bottom">
      <view class="pay-info">
        <text class="pay-label">实付：</text>
        <text class="pay-amount">¥{{ product.price }}</text>
      </view>
      <button class="pay-btn" @click="handlePay" :disabled="!contractAgreed">
        {{ contractAgreed ? '确认支付' : '请先签署合同' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetProductDetail, apiCreateOrder, apiPayOrder } from '@/api/index'
import { useUserStore } from '@/store/user'

const product = ref({})
const contractAgreed = ref(false)
const userStore = useUserStore()

const loadData = async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const productId = page.$page?.options?.productId || page.options?.productId || 'P001'

  try {
    const res = await apiGetProductDetail(productId)
    product.value = res.data || {}
  } catch (e) { console.error(e) }
}

const previewContract = () => {
  uni.navigateTo({
    url: `/pages/contract/sign?merchantName=${encodeURIComponent(product.value.merchantName || '')}&productName=${encodeURIComponent(product.value.name || '')}&amount=${product.value.price || 0}`
  })
}

const handlePay = async () => {
  if (!contractAgreed.value) {
    uni.showToast({ title: '请先阅读并同意服务合同', icon: 'none' }); return
  }

  uni.showLoading({ title: '创建订单中...' })
  try {
    const orderRes = await apiCreateOrder({
      productId: product.value.id,
      merchantId: product.value.merchantId,
      amount: product.value.price
    })

    uni.hideLoading()
    uni.showModal({
      title: '模拟支付',
      content: `订单金额：¥${product.value.price}\n\n此为模拟支付，点击确定完成支付。`,
      success: async (res) => {
        if (res.confirm) {
          uni.showLoading({ title: '支付中...' })
          await apiPayOrder(orderRes.data.orderId)
          uni.hideLoading()
          uni.showToast({ title: '支付成功', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: `/pages/order/detail?id=${orderRes.data.orderId}` })
          }, 1500)
        }
      }
    })
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '创建订单失败', icon: 'none' })
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.confirm-scroll { height: calc(100vh - 120rpx); }
.merchant-bar { display: flex; align-items: center; padding: 24rpx 30rpx; }
.merchant-icon {
  width: 64rpx; height: 64rpx; border-radius: 12rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298); margin-right: 16rpx;
}
.m-icon { color: #fff; font-size: 28rpx; font-weight: 700; }
.merchant-name { font-size: 28rpx; font-weight: 600; color: #333; flex: 1; }
.verified-tag { background: #e8f5e9; color: #2e7d32; font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; }

.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.product-row { display: flex; margin-bottom: 20rpx; }
.product-thumb {
  width: 80rpx; height: 80rpx; border-radius: 12rpx;
  background: #e3f2fd; margin-right: 20rpx; color: #1a3c6e; font-size: 32rpx; font-weight: 600;
}
.product-name { display: block; font-size: 28rpx; font-weight: 500; margin-bottom: 6rpx; }
.price-row { display: flex; justify-content: space-between; padding: 12rpx 0; border-top: 1rpx solid #f5f5f5; }
.label { color: #666; font-size: 26rpx; }
.price { color: #c41e3a; font-size: 28rpx; font-weight: 600; }
.value { color: #333; font-size: 26rpx; }

.contract-desc { display: block; margin-bottom: 16rpx; }
.contract-check { display: flex; align-items: center; }
.check-box {
  width: 36rpx; height: 36rpx; border: 2rpx solid #ccc; border-radius: 6rpx;
  margin-right: 12rpx; display: flex; align-items: center; justify-content: center;
  font-size: 22rpx; color: #fff;
}
.check-box.checked { background: #1a3c6e; border-color: #1a3c6e; }
.check-text { font-size: 26rpx; color: #666; }
.contract-link { font-size: 26rpx; color: #1a3c6e; text-decoration: underline; }

.fund-notice { background: #f0f7ff !important; border: 2rpx solid #c5d4e8; }
.notice-header { display: flex; align-items: center; margin-bottom: 12rpx; }
.shield { font-size: 28rpx; margin-right: 8rpx; }
.notice-title { font-size: 28rpx; font-weight: 600; color: #1a3c6e; }
.notice-text { font-size: 24rpx; color: #555; line-height: 1.8; }

.summary-divider { height: 1rpx; background: #f0f0f0; margin: 12rpx 0; }
.summary-row { display: flex; justify-content: space-between; padding: 8rpx 0; }
.summary-row.total { padding-top: 16rpx; }
.discount { color: #2e7d32; }
.total-price { font-size: 36rpx; font-weight: 700; color: #c41e3a; }

.bottom-safe-lg { height: 160rpx; }

.pay-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; display: flex; align-items: center;
  padding: 16rpx 30rpx; box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}
.pay-info { margin-right: 30rpx; }
.pay-label { font-size: 26rpx; color: #666; }
.pay-amount { font-size: 40rpx; font-weight: 700; color: #c41e3a; }
.pay-btn {
  flex: 1; background: linear-gradient(135deg, #c41e3a, #e63950) !important;
  color: #fff !important; border: none !important; border-radius: 44rpx !important;
  height: 88rpx; line-height: 88rpx; font-size: 32rpx !important; font-weight: 600;
}
.pay-btn[disabled] { opacity: 0.5; }
</style>

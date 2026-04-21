<template>
  <view class="container">
    <scroll-view scroll-y class="contract-scroll">
      <contract-viewer
        :contractNo="contractNo"
        :consumerName="consumerName"
        :merchantName="merchantName"
        :productName="productName"
        :amount="amount"
        :signDate="signDate"
        :showSignature="true"
        :isSigned="isSigned"
      />

      <view class="sign-section" v-if="!isSigned">
        <view class="agree-row" @click="agreed = !agreed">
          <view class="check-box" :class="{ checked: agreed }">
            <text v-if="agreed">&#x2713;</text>
          </view>
          <text class="agree-text">我已仔细阅读并同意以上合同全部条款</text>
        </view>
        <button class="btn-accent sign-btn" @click="handleSign" :disabled="!agreed">
          确认签署合同
        </button>
        <text class="sign-notice text-sm text-gray text-center">
          签署后将通过公证处电子签章系统进行数字签名认证
        </text>
      </view>

      <view class="signed-section" v-else>
        <view class="signed-notice">
          <text class="signed-icon">&#x2705;</text>
          <text class="signed-text">合同已签署</text>
          <text class="signed-time text-sm text-gray">签署时间：{{ signDate }}</text>
        </view>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiSignContract } from '@/api/index'
import { useUserStore } from '@/store/user'
import { formatDate } from '@/utils/format'

const userStore = useUserStore()
const contractNo = ref('')
const consumerName = ref('')
const merchantName = ref('')
const productName = ref('')
const amount = ref('')
const signDate = ref('')
const isSigned = ref(false)
const agreed = ref(false)

const handleSign = async () => {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意合同条款', icon: 'none' }); return
  }

  uni.showLoading({ title: '签署中...' })
  // Simulate signing process
  setTimeout(async () => {
    try {
      await apiSignContract({
        contractNo: contractNo.value,
        merchantName: merchantName.value,
        amount: amount.value
      })
      isSigned.value = true
      signDate.value = formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss')
      uni.hideLoading()
      uni.showToast({ title: '签署成功', icon: 'success' })
    } catch (e) {
      uni.hideLoading()
    }
  }, 1500)
}

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const opts = page.$page?.options || page.options || {}

  contractNo.value = opts.contractNo || 'HT-2024-' + Date.now()
  merchantName.value = decodeURIComponent(opts.merchantName || '商家')
  productName.value = decodeURIComponent(opts.productName || '服务项目')
  amount.value = opts.amount || '0'
  consumerName.value = userStore.userInfo.name || '消费者'

  if (opts.signed === '1') {
    isSigned.value = true
    signDate.value = formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss')
  }
})
</script>

<style lang="scss" scoped>
.contract-scroll { height: 100vh; }
.sign-section { padding: 30rpx; }
.agree-row { display: flex; align-items: center; margin-bottom: 30rpx; }
.check-box {
  width: 40rpx; height: 40rpx; border: 2rpx solid #ccc; border-radius: 6rpx;
  margin-right: 12rpx; display: flex; align-items: center; justify-content: center;
  font-size: 24rpx; color: #fff;
}
.check-box.checked { background: #1a3c6e; border-color: #1a3c6e; }
.agree-text { font-size: 26rpx; color: #333; }
.sign-btn { width: 100%; }
.sign-btn[disabled] { opacity: 0.5; }
.sign-notice { display: block; margin-top: 20rpx; text-align: center; }

.signed-section { padding: 40rpx; }
.signed-notice { text-align: center; }
.signed-icon { display: block; font-size: 60rpx; margin-bottom: 16rpx; }
.signed-text { display: block; font-size: 32rpx; font-weight: 700; color: #2e7d32; margin-bottom: 12rpx; }
.bottom-safe { height: 40rpx; }
</style>

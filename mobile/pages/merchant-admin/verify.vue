<template>
  <view class="container">
    <!-- New Verify -->
    <view class="new-verify card">
      <text class="card-title">消费核销</text>
      <view class="form-group">
        <text class="form-label">客户手机号</text>
        <input class="form-input" v-model="verifyForm.phone" type="number" maxlength="11" placeholder="请输入客户手机号" />
      </view>
      <view class="form-group">
        <text class="form-label">服务项目</text>
        <picker :range="productNames" @change="onProductChange">
          <view class="picker-box">
            <text :class="{ placeholder: !verifyForm.productName }">{{ verifyForm.productName || '请选择服务项目' }}</text>
            <text class="picker-arrow">&#x25BC;</text>
          </view>
        </picker>
      </view>
      <view class="form-group">
        <text class="form-label">核销次数</text>
        <input class="form-input" v-model="verifyForm.sessions" type="number" placeholder="请输入核销次数" />
      </view>
      <view class="form-group">
        <text class="form-label">核销金额</text>
        <input class="form-input" v-model="verifyForm.amount" type="digit" placeholder="自动计算或手动输入" />
      </view>
      <button class="btn-primary" @click="handleVerify">提交核销</button>
    </view>

    <!-- Records -->
    <view class="section-title">核销记录</view>
    <scroll-view scroll-y class="records-scroll">
      <view class="record-item card" v-for="item in records" :key="item.id">
        <view class="record-header">
          <text class="customer">{{ item.customerName }}</text>
          <view class="badge-success">
            <text>已核销</text>
          </view>
        </view>
        <view class="record-info">
          <text class="info-text">{{ item.productName }} × {{ item.sessions }}次</text>
          <text class="amount">¥{{ item.amount }}</text>
        </view>
        <text class="record-time text-sm text-gray">{{ item.time }}</text>
      </view>
      <empty-state v-if="!records.length" text="暂无核销记录" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { apiGetVerifyRecords, apiVerifyConsumption, apiGetMerchantProducts } from '@/api/index'

const records = ref([])
const productNames = ref([])
const products = ref([])

const verifyForm = reactive({
  phone: '', productName: '', sessions: '1', amount: ''
})

const loadData = async () => {
  try {
    const [verifyRes, productRes] = await Promise.all([
      apiGetVerifyRecords(),
      apiGetMerchantProducts()
    ])
    records.value = verifyRes.data.list || []
    products.value = productRes.data.list || []
    productNames.value = products.value.map(p => p.name)
  } catch (e) { console.error(e) }
}

const onProductChange = (e) => {
  const idx = e.detail.value
  verifyForm.productName = productNames.value[idx]
  const p = products.value[idx]
  if (p && p.sessions > 0) {
    verifyForm.amount = (p.price / p.sessions * parseInt(verifyForm.sessions || 1)).toFixed(2)
  }
}

const handleVerify = async () => {
  if (!verifyForm.phone) { uni.showToast({ title: '请输入客户手机号', icon: 'none' }); return }
  if (!verifyForm.productName) { uni.showToast({ title: '请选择服务项目', icon: 'none' }); return }
  if (!verifyForm.amount) { uni.showToast({ title: '请输入核销金额', icon: 'none' }); return }

  uni.showLoading({ title: '核销中...' })
  try {
    await apiVerifyConsumption(verifyForm)
    uni.hideLoading()
    uni.showToast({ title: '核销成功', icon: 'success' })
    records.value.unshift({
      id: 'V' + Date.now(),
      customerName: verifyForm.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2'),
      productName: verifyForm.productName,
      amount: parseFloat(verifyForm.amount),
      sessions: parseInt(verifyForm.sessions),
      time: new Date().toLocaleString(),
      status: 'verified'
    })
    verifyForm.phone = ''
    verifyForm.sessions = '1'
    verifyForm.amount = ''
  } catch (e) { uni.hideLoading() }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 24rpx; }
.form-group { margin-bottom: 24rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 10rpx; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 22rpx; font-size: 28rpx; width: 100%; box-sizing: border-box;
}
.picker-box {
  display: flex; justify-content: space-between; align-items: center;
  border: 2rpx solid #e0e0e0; border-radius: 12rpx; padding: 22rpx;
}
.placeholder { color: #999; }
.picker-arrow { color: #ccc; }

.records-scroll { height: calc(100vh - 600px); }
.record-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.customer { font-size: 28rpx; font-weight: 500; color: #333; }
.record-info { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.info-text { font-size: 26rpx; color: #666; }
.amount { font-size: 28rpx; font-weight: 600; color: #2e7d32; }
</style>

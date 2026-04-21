<template>
  <view class="container">
    <scroll-view scroll-y class="form-scroll">
      <view class="form-card card">
        <text class="card-title">投诉信息</text>

        <view class="form-group">
          <text class="form-label">投诉商家 *</text>
          <picker :range="merchantNames" @change="onMerchantChange">
            <view class="picker-box">
              <text :class="{ placeholder: !form.merchantName }">{{ form.merchantName || '请选择商家' }}</text>
              <text class="picker-arrow">&#x25BC;</text>
            </view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">关联订单（选填）</text>
          <input class="form-input" v-model="form.orderId" placeholder="请输入订单编号" />
        </view>

        <view class="form-group">
          <text class="form-label">投诉类型 *</text>
          <view class="type-list">
            <view class="type-item" v-for="(t, i) in complaintTypes" :key="i"
              :class="{ selected: form.type === t }" @click="form.type = t">
              <text>{{ t }}</text>
            </view>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">投诉描述 *</text>
          <textarea class="form-textarea" v-model="form.description" placeholder="请详细描述您遇到的问题（至少20字）" maxlength="500" />
          <text class="char-count text-sm text-gray">{{ form.description.length }}/500</text>
        </view>

        <view class="form-group">
          <text class="form-label">联系电话 *</text>
          <input class="form-input" v-model="form.phone" type="number" maxlength="11" placeholder="请输入联系电话" />
        </view>
      </view>

      <view class="notice card">
        <text class="notice-title">&#x2139; 投诉须知</text>
        <text class="notice-text">1. 公证处将在收到投诉后2个工作日内介入处理</text>
        <text class="notice-text">2. 请如实描述问题，提供准确的订单信息</text>
        <text class="notice-text">3. 处理结果将通过消息通知告知您</text>
      </view>

      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <view class="submit-bar safe-bottom">
      <button class="submit-btn btn-primary" @click="handleSubmit">提交投诉</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { apiSubmitComplaint, apiGetMerchants } from '@/api/index'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const merchantNames = ref([])
const merchants = ref([])

const form = reactive({
  merchantName: '', orderId: '', type: '', description: '', phone: userStore.userInfo.phone || ''
})

const complaintTypes = ['服务质量', '虚假宣传', '退费纠纷', '商家失联', '其他问题']

const loadMerchants = async () => {
  try {
    const res = await apiGetMerchants()
    merchants.value = res.data.list || []
    merchantNames.value = merchants.value.map(m => m.name)
  } catch (e) { console.error(e) }
}

const onMerchantChange = (e) => {
  form.merchantName = merchantNames.value[e.detail.value]
}

const handleSubmit = async () => {
  if (!form.merchantName) { uni.showToast({ title: '请选择商家', icon: 'none' }); return }
  if (!form.type) { uni.showToast({ title: '请选择投诉类型', icon: 'none' }); return }
  if (form.description.length < 20) { uni.showToast({ title: '投诉描述至少20字', icon: 'none' }); return }
  if (!form.phone) { uni.showToast({ title: '请输入联系电话', icon: 'none' }); return }

  uni.showLoading({ title: '提交中...' })
  try {
    await apiSubmitComplaint(form)
    uni.hideLoading()
    uni.showToast({ title: '投诉已提交', icon: 'success' })
    setTimeout(() => { uni.navigateBack() }, 1500)
  } catch (e) {
    uni.hideLoading()
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const merchantName = page.$page?.options?.merchantName || page.options?.merchantName
  const orderId = page.$page?.options?.orderId || page.options?.orderId
  if (merchantName) form.merchantName = decodeURIComponent(merchantName)
  if (orderId) form.orderId = orderId
  loadMerchants()
})
</script>

<style lang="scss" scoped>
.form-scroll { height: calc(100vh - 120rpx); }
.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 24rpx; }
.form-group { margin-bottom: 30rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; font-weight: 500; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 22rpx; font-size: 28rpx; width: 100%; box-sizing: border-box;
}
.picker-box {
  display: flex; justify-content: space-between; align-items: center;
  border: 2rpx solid #e0e0e0; border-radius: 12rpx; padding: 22rpx;
}
.placeholder { color: #999; }
.picker-arrow { color: #ccc; font-size: 22rpx; }

.type-list { display: flex; flex-wrap: wrap; gap: 16rpx; }
.type-item {
  padding: 14rpx 28rpx; border: 2rpx solid #e0e0e0; border-radius: 30rpx;
  font-size: 26rpx; color: #666;
}
.type-item.selected { border-color: #1a3c6e; color: #1a3c6e; background: #e3f2fd; }

.form-textarea {
  width: 100%; height: 200rpx; border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 16rpx; font-size: 26rpx; box-sizing: border-box;
}
.char-count { display: block; text-align: right; margin-top: 8rpx; }

.notice { background: #f0f7ff !important; }
.notice-title { display: block; font-size: 28rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 12rpx; }
.notice-text { display: block; font-size: 24rpx; color: #555; line-height: 2; }

.bottom-safe-lg { height: 140rpx; }
.submit-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 16rpx 30rpx; background: #fff; box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}
.submit-btn { width: 100%; }
</style>

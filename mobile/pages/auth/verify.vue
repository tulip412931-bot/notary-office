<template>
  <view class="container">
    <scroll-view scroll-y class="verify-scroll">
      <!-- Status -->
      <view class="status-card" :class="{ verified: isVerified }">
        <text class="status-icon">{{ isVerified ? '&#x2705;' : '&#x1F4CB;' }}</text>
        <text class="status-text">{{ isVerified ? '已完成实名认证' : '未实名认证' }}</text>
        <text class="status-sub" v-if="isVerified">认证时间：2024-03-10</text>
        <text class="status-sub" v-else>完成实名认证后可使用全部功能</text>
      </view>

      <view v-if="!isVerified">
        <!-- ID Card -->
        <view class="card">
          <text class="card-title">身份证信息</text>
          <view class="form-group">
            <text class="form-label">真实姓名</text>
            <input class="form-input" v-model="form.name" placeholder="请输入真实姓名" />
          </view>
          <view class="form-group">
            <text class="form-label">身份证号</text>
            <input class="form-input" v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
          </view>
        </view>

        <!-- ID Photo Upload (Mock) -->
        <view class="card">
          <text class="card-title">证件照片</text>
          <view class="upload-area">
            <view class="upload-box" @click="mockUpload('front')">
              <text class="upload-icon" v-if="!frontUploaded">&#x1F4F7;</text>
              <text class="upload-done" v-else>&#x2705;</text>
              <text class="upload-text">{{ frontUploaded ? '正面已上传' : '身份证正面' }}</text>
            </view>
            <view class="upload-box" @click="mockUpload('back')">
              <text class="upload-icon" v-if="!backUploaded">&#x1F4F7;</text>
              <text class="upload-done" v-else>&#x2705;</text>
              <text class="upload-text">{{ backUploaded ? '反面已上传' : '身份证反面' }}</text>
            </view>
          </view>
        </view>

        <!-- Face Recognition (Mock) -->
        <view class="card">
          <text class="card-title">人脸识别</text>
          <view class="face-area" @click="mockFace">
            <view class="face-circle" :class="{ done: faceVerified }">
              <text class="face-icon">{{ faceVerified ? '&#x2705;' : '&#x1F464;' }}</text>
            </view>
            <text class="face-text">{{ faceVerified ? '人脸识别已通过' : '点击进行人脸识别' }}</text>
          </view>
        </view>

        <view class="submit-area">
          <button class="btn-primary" @click="handleVerify">提交认证</button>
        </view>
      </view>

      <view v-else class="verified-info card">
        <view class="info-item">
          <text class="label">姓名</text>
          <text class="value">张**</text>
        </view>
        <view class="info-item">
          <text class="label">身份证号</text>
          <text class="value">110***********1234</text>
        </view>
        <view class="info-item">
          <text class="label">认证状态</text>
          <text class="value safe">已认证</text>
        </view>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { apiVerifyIdentity } from '@/api/index'

const userStore = useUserStore()
const isVerified = computed(() => !!userStore.userInfo.verified)

const form = reactive({ name: '', idCard: '' })
const frontUploaded = ref(false)
const backUploaded = ref(false)
const faceVerified = ref(false)

const mockUpload = (side) => {
  uni.showLoading({ title: '上传中...' })
  setTimeout(() => {
    uni.hideLoading()
    if (side === 'front') frontUploaded.value = true
    else backUploaded.value = true
    uni.showToast({ title: '上传成功', icon: 'success' })
  }, 1000)
}

const mockFace = () => {
  uni.showLoading({ title: '人脸识别中...' })
  setTimeout(() => {
    uni.hideLoading()
    faceVerified.value = true
    uni.showToast({ title: '识别通过', icon: 'success' })
  }, 2000)
}

const handleVerify = async () => {
  if (!form.name) { uni.showToast({ title: '请输入姓名', icon: 'none' }); return }
  if (!form.idCard || form.idCard.length < 18) { uni.showToast({ title: '请输入正确的身份证号', icon: 'none' }); return }
  if (!frontUploaded.value || !backUploaded.value) { uni.showToast({ title: '请上传身份证照片', icon: 'none' }); return }
  if (!faceVerified.value) { uni.showToast({ title: '请完成人脸识别', icon: 'none' }); return }

  uni.showLoading({ title: '认证中...' })
  try {
    await apiVerifyIdentity(form)
    userStore.updateUserInfo({ verified: true })
    uni.hideLoading()
    uni.showToast({ title: '认证成功', icon: 'success' })
  } catch (e) { uni.hideLoading() }
}
</script>

<style lang="scss" scoped>
.verify-scroll { height: 100vh; }
.status-card {
  text-align: center; padding: 50rpx 30rpx;
  background: linear-gradient(135deg, #ef6c00, #ff9800);
}
.status-card.verified { background: linear-gradient(135deg, #2e7d32, #43a047); }
.status-icon { display: block; font-size: 60rpx; margin-bottom: 16rpx; }
.status-text { display: block; font-size: 32rpx; font-weight: 700; color: #fff; margin-bottom: 8rpx; }
.status-sub { display: block; font-size: 24rpx; color: rgba(255,255,255,0.8); }

.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.form-group { margin-bottom: 24rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 10rpx; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 22rpx; font-size: 28rpx; width: 100%; box-sizing: border-box;
}

.upload-area { display: flex; gap: 20rpx; }
.upload-box {
  flex: 1; height: 200rpx; border: 2rpx dashed #ccc; border-radius: 12rpx;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
}
.upload-icon { font-size: 48rpx; margin-bottom: 12rpx; }
.upload-done { font-size: 48rpx; margin-bottom: 12rpx; }
.upload-text { font-size: 24rpx; color: #999; }

.face-area { text-align: center; padding: 20rpx 0; }
.face-circle {
  width: 160rpx; height: 160rpx; border-radius: 50%;
  border: 4rpx dashed #ccc; display: flex; align-items: center; justify-content: center;
  margin: 0 auto 20rpx;
}
.face-circle.done { border-color: #2e7d32; border-style: solid; }
.face-icon { font-size: 60rpx; }
.face-text { font-size: 26rpx; color: #666; }

.submit-area { padding: 30rpx; }

.verified-info .info-item {
  display: flex; justify-content: space-between; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.verified-info .info-item:last-child { border-bottom: none; }
.label { color: #999; font-size: 26rpx; }
.value { color: #333; font-size: 26rpx; }
.safe { color: #2e7d32; font-weight: 600; }
.bottom-safe { height: 40rpx; }
</style>

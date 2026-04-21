<template>
  <view class="container">
    <scroll-view scroll-y class="help-scroll">
      <!-- Header -->
      <view class="help-header">
        <text class="header-icon">&#x1F6E1;</text>
        <text class="header-title">帮助中心</text>
        <text class="header-sub">预付费资金监管常见问题</text>
      </view>

      <!-- FAQ List -->
      <view class="faq-list">
        <view class="faq-item" v-for="(item, i) in faqList" :key="i">
          <view class="faq-question" @click="toggleFaq(i)">
            <text class="q-icon">Q</text>
            <text class="q-text flex-1">{{ item.q }}</text>
            <text class="q-arrow" :class="{ open: openIndex === i }">&#x276F;</text>
          </view>
          <view class="faq-answer" v-if="openIndex === i">
            <text class="a-icon">A</text>
            <text class="a-text">{{ item.a }}</text>
          </view>
        </view>
      </view>

      <!-- Contact -->
      <view class="contact-card card">
        <text class="card-title">联系我们</text>
        <view class="contact-item" @click="callPhone('400-888-9999')">
          <text class="contact-icon">&#x1F4DE;</text>
          <view class="contact-info flex-1">
            <text class="contact-label">客服热线</text>
            <text class="contact-value">400-888-9999</text>
          </view>
          <text class="contact-arrow">&#x276F;</text>
        </view>
        <view class="contact-item" @click="callPhone('010-88886666')">
          <text class="contact-icon">&#x1F3DB;</text>
          <view class="contact-info flex-1">
            <text class="contact-label">公证处电话</text>
            <text class="contact-value">010-88886666</text>
          </view>
          <text class="contact-arrow">&#x276F;</text>
        </view>
        <view class="contact-item">
          <text class="contact-icon">&#x1F4CD;</text>
          <view class="contact-info flex-1">
            <text class="contact-label">公证处地址</text>
            <text class="contact-value">北京市朝阳区建国门外大街1号</text>
          </view>
        </view>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetFAQ } from '@/api/index'

const faqList = ref([])
const openIndex = ref(-1)

const toggleFaq = (i) => {
  openIndex.value = openIndex.value === i ? -1 : i
}

const callPhone = (phone) => {
  uni.makePhoneCall({ phoneNumber: phone })
}

onMounted(async () => {
  try {
    const res = await apiGetFAQ()
    faqList.value = res.data.list || []
  } catch (e) { console.error(e) }
})
</script>

<style lang="scss" scoped>
.help-scroll { height: 100vh; }
.help-header {
  text-align: center; padding: 50rpx 30rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
}
.header-icon { display: block; font-size: 60rpx; margin-bottom: 16rpx; }
.header-title { display: block; font-size: 36rpx; font-weight: 700; color: #fff; margin-bottom: 8rpx; }
.header-sub { display: block; font-size: 26rpx; color: rgba(255,255,255,0.7); }

.faq-list { margin: 20rpx 0; }
.faq-item { background: #fff; margin-bottom: 2rpx; }
.faq-question {
  display: flex; align-items: center; padding: 30rpx;
}
.q-icon {
  width: 44rpx; height: 44rpx; border-radius: 50%; background: #1a3c6e;
  color: #fff; font-size: 24rpx; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  margin-right: 16rpx; flex-shrink: 0;
}
.q-text { font-size: 28rpx; color: #333; font-weight: 500; }
.q-arrow { color: #ccc; font-size: 24rpx; transform: rotate(90deg); transition: transform 0.3s; }
.q-arrow.open { transform: rotate(270deg); }
.faq-answer {
  display: flex; padding: 0 30rpx 30rpx;
  background: #f8faff;
}
.a-icon {
  width: 44rpx; height: 44rpx; border-radius: 50%; background: #e8f5e9;
  color: #2e7d32; font-size: 24rpx; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  margin-right: 16rpx; flex-shrink: 0;
}
.a-text { font-size: 26rpx; color: #555; line-height: 1.8; flex: 1; }

.card-title { display: block; font-size: 30rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 20rpx; }
.contact-item {
  display: flex; align-items: center; padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.contact-item:last-child { border-bottom: none; }
.contact-icon { font-size: 36rpx; margin-right: 20rpx; }
.contact-label { display: block; font-size: 24rpx; color: #999; }
.contact-value { display: block; font-size: 28rpx; color: #333; }
.contact-arrow { color: #ccc; font-size: 24rpx; }
.bottom-safe { height: 40rpx; }
</style>

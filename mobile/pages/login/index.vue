<template>
  <view class="container">
    <view class="login-header">
      <view class="logo-area">
        <text class="logo-emblem">&#x2696;</text>
        <text class="logo-title">公证预付费监管平台</text>
        <text class="logo-sub">安全 · 可信 · 有保障</text>
      </view>
    </view>

    <view class="login-form">
      <view class="form-group">
        <text class="form-label">手机号</text>
        <input class="form-input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
      </view>
      <view class="form-group">
        <text class="form-label">密码</text>
        <input class="form-input" v-model="password" type="password" placeholder="请输入密码" />
      </view>

      <button class="btn-primary login-btn" @click="handleLogin">登 录</button>

      <view class="login-links">
        <text class="link" @click="goRegister">注册账号</text>
        <text class="link" @click="goMerchantLogin">商家登录</text>
      </view>

      <view class="agreement">
        <view class="check-box" :class="{ checked: agreed }" @click="agreed = !agreed">
          <text v-if="agreed">&#x2713;</text>
        </view>
        <text class="agreement-text">我已阅读并同意《用户服务协议》和《隐私政策》</text>
      </view>
    </view>

    <view class="gov-footer">
      <text class="footer-text">北京市东方公证处 监制</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { apiLogin, apiMerchantLogin } from '@/api/index'

const phone = ref('13800138000')
const password = ref('123456')
const agreed = ref(true)
const userStore = useUserStore()
const redirectUrl = ref('')

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1]
  const opts = page.$page?.options || page.options || {}
  if (opts.redirect) {
    redirectUrl.value = decodeURIComponent(opts.redirect)
  }
})

const tabBarPages = ['/pages/index/index', '/pages/order/list', '/pages/merchant/list', '/pages/mine/index']

const navigateAfterLogin = (url) => {
  if (tabBarPages.includes(url)) {
    uni.switchTab({ url })
  } else {
    uni.redirectTo({ url })
  }
}

const handleLogin = async () => {
  if (!phone.value || phone.value.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' }); return
  }
  if (!password.value) {
    uni.showToast({ title: '请输入密码', icon: 'none' }); return
  }
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' }); return
  }

  uni.showLoading({ title: '登录中...' })
  try {
    const res = await apiLogin({ phone: phone.value, password: password.value })
    userStore.login(res.data)
    uni.hideLoading()
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      navigateAfterLogin(redirectUrl.value || '/pages/index/index')
    }, 1000)
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: '登录失败', icon: 'none' })
  }
}

const goRegister = () => {
  uni.navigateTo({ url: '/pages/register/index' })
}

const goMerchantLogin = async () => {
  uni.showLoading({ title: '商家登录中...' })
  try {
    const res = await apiMerchantLogin({ phone: '13900139000', password: '123456' })
    userStore.login(res.data)
    uni.hideLoading()
    uni.showToast({ title: '商家登录成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/merchant-admin/index' })
    }, 1000)
  } catch (e) {
    uni.hideLoading()
  }
}
</script>

<style lang="scss" scoped>
.container { background: #fff; min-height: 100vh; }
.login-header {
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  padding: 80rpx 30rpx 60rpx; text-align: center;
}
.logo-area { display: flex; flex-direction: column; align-items: center; }
.logo-emblem { font-size: 80rpx; margin-bottom: 20rpx; }
.logo-title { font-size: 40rpx; font-weight: 700; color: #fff; margin-bottom: 12rpx; }
.logo-sub { font-size: 26rpx; color: rgba(255,255,255,0.7); }

.login-form { padding: 60rpx 50rpx; }
.form-group { margin-bottom: 40rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 16rpx; font-weight: 500; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 24rpx; font-size: 30rpx; width: 100%; box-sizing: border-box;
}
.login-btn { margin-top: 20rpx; }

.login-links {
  display: flex; justify-content: space-between; margin-top: 30rpx; padding: 0 10rpx;
}
.link { color: #1a3c6e; font-size: 28rpx; }

.agreement {
  display: flex; align-items: flex-start; margin-top: 40rpx;
}
.check-box {
  width: 36rpx; height: 36rpx; border: 2rpx solid #ccc; border-radius: 6rpx;
  margin-right: 12rpx; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; margin-top: 2rpx; font-size: 22rpx; color: #fff;
}
.check-box.checked { background: #1a3c6e; border-color: #1a3c6e; }
.agreement-text { font-size: 24rpx; color: #999; line-height: 1.5; }

.gov-footer { text-align: center; padding: 60rpx 0 40rpx; }
.footer-text { font-size: 24rpx; color: #ccc; }
</style>

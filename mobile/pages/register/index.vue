<template>
  <view class="container">
    <view class="reg-header">
      <text class="reg-emblem">&#x2696;</text>
      <text class="reg-title">注册账号</text>
    </view>

    <view class="reg-form">
      <view class="form-group">
        <text class="form-label">姓名</text>
        <input class="form-input" v-model="form.name" placeholder="请输入真实姓名" />
      </view>
      <view class="form-group">
        <text class="form-label">手机号</text>
        <input class="form-input" v-model="form.phone" type="number" maxlength="11" placeholder="请输入手机号" />
      </view>
      <view class="form-group">
        <text class="form-label">验证码</text>
        <view class="code-row">
          <input class="form-input flex-1" v-model="form.code" type="number" maxlength="6" placeholder="请输入验证码" />
          <button class="code-btn" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
          </button>
        </view>
      </view>
      <view class="form-group">
        <text class="form-label">设置密码</text>
        <input class="form-input" v-model="form.password" type="password" placeholder="请设置6-20位密码" />
      </view>
      <view class="form-group">
        <text class="form-label">确认密码</text>
        <input class="form-input" v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
      </view>

      <view class="agreement">
        <view class="check-box" :class="{ checked: agreed }" @click="agreed = !agreed">
          <text v-if="agreed">&#x2713;</text>
        </view>
        <text class="agreement-text">我已阅读并同意《用户服务协议》和《隐私政策》</text>
      </view>

      <button class="btn-primary reg-btn" @click="handleRegister">注 册</button>

      <view class="login-link" @click="goLogin">
        <text>已有账号？去登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { apiRegister } from '@/api/index'

const userStore = useUserStore()
const agreed = ref(true)
const countdown = ref(0)
let timer = null

const form = reactive({
  name: '', phone: '', code: '', password: '', confirmPassword: ''
})

const sendCode = () => {
  if (!form.phone || form.phone.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' }); return
  }
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  uni.showToast({ title: '验证码已发送', icon: 'none' })
}

const handleRegister = async () => {
  if (!form.name) { uni.showToast({ title: '请输入姓名', icon: 'none' }); return }
  if (!form.phone || form.phone.length !== 11) { uni.showToast({ title: '请输入正确的手机号', icon: 'none' }); return }
  if (!form.code) { uni.showToast({ title: '请输入验证码', icon: 'none' }); return }
  if (!form.password || form.password.length < 6) { uni.showToast({ title: '密码至少6位', icon: 'none' }); return }
  if (form.password !== form.confirmPassword) { uni.showToast({ title: '两次密码不一致', icon: 'none' }); return }
  if (!agreed.value) { uni.showToast({ title: '请先同意用户协议', icon: 'none' }); return }

  uni.showLoading({ title: '注册中...' })
  try {
    const res = await apiRegister(form)
    userStore.login(res.data)
    uni.hideLoading()
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => { uni.switchTab({ url: '/pages/index/index' }) }, 1000)
  } catch (e) {
    uni.hideLoading()
  }
}

const goLogin = () => { uni.navigateBack() }
</script>

<style lang="scss" scoped>
.container { background: #fff; min-height: 100vh; }
.reg-header {
  text-align: center; padding: 60rpx 0 40rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
}
.reg-emblem { font-size: 60rpx; display: block; margin-bottom: 16rpx; }
.reg-title { font-size: 36rpx; font-weight: 700; color: #fff; }
.reg-form { padding: 40rpx 50rpx; }
.form-group { margin-bottom: 32rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; font-weight: 500; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 22rpx; font-size: 28rpx;
}
.code-row { display: flex; align-items: center; gap: 16rpx; }
.code-btn {
  font-size: 24rpx; color: #1a3c6e; background: #e3f2fd;
  border: none; border-radius: 12rpx; padding: 22rpx 20rpx; white-space: nowrap;
}
.agreement { display: flex; align-items: flex-start; margin: 30rpx 0; }
.check-box {
  width: 36rpx; height: 36rpx; border: 2rpx solid #ccc; border-radius: 6rpx;
  margin-right: 12rpx; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; font-size: 22rpx; color: #fff;
}
.check-box.checked { background: #1a3c6e; border-color: #1a3c6e; }
.agreement-text { font-size: 24rpx; color: #999; line-height: 1.5; }
.reg-btn { margin-top: 10rpx; }
.login-link { text-align: center; margin-top: 30rpx; color: #1a3c6e; font-size: 28rpx; }
</style>

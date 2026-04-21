<script>
export default {
  onLaunch() {
    console.log('公证预付费监管平台启动')
  },
  onShow() {},
  onHide() {}
}
</script>

<style lang="scss">
@import './uni.scss';

page {
  background-color: #f5f6fa;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.6;
}

.container {
  min-height: 100vh;
  background-color: #f5f6fa;
}

/* Common utility classes */
.flex-row { display: flex; flex-direction: row; align-items: center; }
.flex-col { display: flex; flex-direction: column; }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.flex-1 { flex: 1; }

.text-primary { color: #1a3c6e; }
.text-accent { color: #c41e3a; }
.text-gray { color: #999999; }
.text-dark { color: #333333; }
.text-sm { font-size: 24rpx; }
.text-md { font-size: 28rpx; }
.text-lg { font-size: 32rpx; }
.text-xl { font-size: 36rpx; }
.text-bold { font-weight: 600; }
.text-center { text-align: center; }

.mt-10 { margin-top: 10rpx; }
.mt-20 { margin-top: 20rpx; }
.mt-30 { margin-top: 30rpx; }
.mb-10 { margin-bottom: 10rpx; }
.mb-20 { margin-bottom: 20rpx; }
.mb-30 { margin-bottom: 30rpx; }
.ml-10 { margin-left: 10rpx; }
.mr-10 { margin-right: 10rpx; }
.p-20 { padding: 20rpx; }
.p-30 { padding: 30rpx; }
.px-30 { padding-left: 30rpx; padding-right: 30rpx; }

.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 20rpx 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.btn-primary {
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  color: #ffffff;
  border: none;
  border-radius: 12rpx;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
}

.btn-accent {
  background: linear-gradient(135deg, #c41e3a, #e63950);
  color: #ffffff;
  border: none;
  border-radius: 12rpx;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
}

.btn-outline {
  background: #ffffff;
  color: #1a3c6e;
  border: 2rpx solid #1a3c6e;
  border-radius: 12rpx;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  text-align: center;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1a3c6e;
  padding: 30rpx 30rpx 20rpx;
  display: flex;
  align-items: center;

  &::before {
    content: '';
    width: 6rpx;
    height: 32rpx;
    background: linear-gradient(180deg, #1a3c6e, #2a5298);
    border-radius: 3rpx;
    margin-right: 16rpx;
  }
}

.safe-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

.divider {
  height: 1rpx;
  background-color: #f0f0f0;
  margin: 0 30rpx;
}

/* Badge styles */
.badge-success { background-color: #e8f5e9; color: #2e7d32; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.badge-warning { background-color: #fff3e0; color: #ef6c00; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.badge-danger { background-color: #fce4ec; color: #c62828; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.badge-info { background-color: #e3f2fd; color: #1565c0; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.badge-gray { background-color: #f5f5f5; color: #757575; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
</style>

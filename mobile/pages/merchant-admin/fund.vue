<template>
  <view class="container">
    <!-- Summary -->
    <view class="fund-header">
      <view class="header-card">
        <text class="header-label">监管账户余额</text>
        <text class="header-amount">¥{{ formatMoney(balance) }}</text>
        <text class="header-sub">公证处托管 · 招商银行 ****8866</text>
      </view>
    </view>

    <!-- Tabs -->
    <view class="tab-bar">
      <view class="tab" :class="{ active: currentTab === 'settlement' }" @click="currentTab = 'settlement'">
        <text>结算记录</text>
      </view>
      <view class="tab" :class="{ active: currentTab === 'detail' }" @click="currentTab = 'detail'">
        <text>资金明细</text>
      </view>
    </view>

    <scroll-view scroll-y class="list-scroll">
      <!-- Settlements -->
      <view v-if="currentTab === 'settlement'">
        <view class="settle-item card" v-for="item in settlements" :key="item.id">
          <view class="settle-header">
            <text class="settle-period">{{ item.period }}</text>
            <view :class="item.status === 'settled' ? 'badge-success' : 'badge-warning'">
              <text>{{ item.status === 'settled' ? '已结算' : '待结算' }}</text>
            </view>
          </view>
          <view class="settle-info">
            <view class="info-col">
              <text class="info-label">结算金额</text>
              <text class="info-value amount">¥{{ formatMoney(item.amount) }}</text>
            </view>
            <view class="info-col">
              <text class="info-label">核销次数</text>
              <text class="info-value">{{ item.verifyCount }}次</text>
            </view>
          </view>
          <text class="settle-time text-sm text-gray" v-if="item.settleTime">
            结算时间：{{ item.settleTime }}
          </text>
        </view>
      </view>

      <!-- Detail (reuse fund records concept) -->
      <view v-if="currentTab === 'detail'">
        <view class="detail-item" v-for="(item, i) in detailRecords" :key="i">
          <view class="detail-left">
            <text class="detail-title">{{ item.title }}</text>
            <text class="detail-sub text-sm text-gray">{{ item.desc }}</text>
          </view>
          <view class="detail-right">
            <text class="detail-amount" :class="{ income: item.amount > 0 }">
              {{ item.amount > 0 ? '+' : '' }}¥{{ formatMoney(Math.abs(item.amount)) }}
            </text>
            <text class="detail-time text-sm text-gray">{{ item.time }}</text>
          </view>
        </view>
      </view>

      <view class="bottom-safe"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiGetSettlements, apiGetMerchantStats } from '@/api/index'
import { formatMoney as fm } from '@/utils/format'

const currentTab = ref('settlement')
const settlements = ref([])
const formatMoney = fm
const balance = ref(0)

const detailRecords = ref([
  { title: '核销收入-年卡会员', desc: '张** 消费核销', amount: 10.88, time: '08-15 18:30' },
  { title: '核销收入-私教课程', desc: '李** 消费核销', amount: 340, time: '08-15 16:00' },
  { title: '核销收入-瑜伽季卡', desc: '王** 消费核销', amount: 74.44, time: '08-15 10:30' },
  { title: '月度结算到账', desc: '2024年7月结算', amount: 168000, time: '08-05 10:00' },
  { title: '退款扣除', desc: '赵** 退费', amount: -2388, time: '07-28 15:00' },
  { title: '核销收入-年卡会员', desc: '赵** 消费核销', amount: 10.88, time: '08-14 19:00' }
])

const loadData = async () => {
  try {
    const [res, statsRes] = await Promise.all([
      apiGetSettlements(),
      apiGetMerchantStats()
    ])
    settlements.value = res.data.list || []
    balance.value = statsRes.data.balance || 0
  } catch (e) { console.error(e) }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.fund-header {
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  padding: 40rpx 30rpx;
}
.header-card { text-align: center; }
.header-label { display: block; font-size: 26rpx; color: rgba(255,255,255,0.7); margin-bottom: 12rpx; }
.header-amount { display: block; font-size: 48rpx; font-weight: 700; color: #fff; margin-bottom: 12rpx; }
.header-sub { display: block; font-size: 22rpx; color: rgba(255,255,255,0.5); }

.tab-bar { display: flex; background: #fff; border-bottom: 1rpx solid #f0f0f0; }
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

.list-scroll { height: calc(100vh - 320px); }
.settle-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.settle-period { font-size: 28rpx; font-weight: 600; color: #333; }
.settle-info { display: flex; gap: 40rpx; margin-bottom: 12rpx; }
.info-col { }
.info-label { display: block; font-size: 22rpx; color: #999; margin-bottom: 4rpx; }
.info-value { display: block; font-size: 28rpx; color: #333; font-weight: 500; }
.amount { color: #c41e3a; }

.detail-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24rpx 30rpx; background: #fff; border-bottom: 1rpx solid #f5f5f5;
}
.detail-title { display: block; font-size: 28rpx; color: #333; margin-bottom: 4rpx; }
.detail-right { text-align: right; }
.detail-amount { display: block; font-size: 30rpx; font-weight: 600; color: #333; }
.detail-amount.income { color: #2e7d32; }
.bottom-safe { height: 40rpx; }
</style>

<template>
  <view class="contract-viewer">
    <scroll-view scroll-y class="contract-scroll">
      <view class="contract-header text-center">
        <text class="contract-emblem">&#x2696;</text>
        <text class="contract-title">预付费资金监管服务合同</text>
        <text class="contract-no text-sm text-gray">合同编号：{{ contractNo }}</text>
      </view>

      <view class="contract-body">
        <view class="contract-section">
          <text class="section-label">甲方（消费者）：</text>
          <text class="section-value">{{ consumerName }}</text>
        </view>
        <view class="contract-section">
          <text class="section-label">乙方（经营者）：</text>
          <text class="section-value">{{ merchantName }}</text>
        </view>
        <view class="contract-section">
          <text class="section-label">监管方（公证处）：</text>
          <text class="section-value">北京市东方公证处</text>
        </view>

        <view class="contract-divider"></view>

        <view class="contract-clause" v-for="(clause, i) in clauses" :key="i">
          <text class="clause-title">第{{ numToChinese(i + 1) }}条 {{ clause.title }}</text>
          <text class="clause-content">{{ clause.content }}</text>
        </view>

        <view class="contract-divider"></view>

        <view class="contract-section">
          <text class="section-label">服务项目：</text>
          <text class="section-value">{{ productName }}</text>
        </view>
        <view class="contract-section">
          <text class="section-label">合同金额：</text>
          <text class="section-value amount-text">¥{{ amount }}</text>
        </view>
        <view class="contract-section">
          <text class="section-label">签署日期：</text>
          <text class="section-value">{{ signDate }}</text>
        </view>
      </view>

      <view class="signature-area" v-if="showSignature">
        <view class="signature-row">
          <view class="signature-block">
            <text class="sig-label">甲方签章</text>
            <view class="sig-box" :class="{ signed: isSigned }">
              <text v-if="isSigned" class="sig-text">{{ consumerName }}（电子签章）</text>
              <text v-else class="sig-placeholder">待签署</text>
            </view>
          </view>
          <view class="signature-block">
            <text class="sig-label">公证处监管章</text>
            <view class="sig-box signed notary-seal">
              <text class="seal-text">北京市东方公证处\n电子公证专用章</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
const props = defineProps({
  contractNo: { type: String, default: '' },
  consumerName: { type: String, default: '' },
  merchantName: { type: String, default: '' },
  productName: { type: String, default: '' },
  amount: { type: [String, Number], default: '' },
  signDate: { type: String, default: '' },
  showSignature: { type: Boolean, default: true },
  isSigned: { type: Boolean, default: false }
})

const numToChinese = (n) => {
  const chars = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
  return n <= 10 ? chars[n - 1] : n.toString()
}

const clauses = [
  { title: '服务内容', content: '乙方应按照约定向甲方提供相应的服务，确保服务质量符合行业标准。服务内容、次数、期限等以本合同附件及乙方公示的服务标准为准。' },
  { title: '资金监管', content: '甲方支付的预付费用由监管方（公证处）进行第三方资金监管。款项存入公证处指定监管账户，乙方不得直接接触或挪用该资金。每次消费核销后，监管方按实际消费金额向乙方拨付。' },
  { title: '消费核销', content: '乙方每次为甲方提供服务后，应在本平台进行消费核销。核销需经甲方确认后生效。核销后对应金额由监管方拨付给乙方。' },
  { title: '退费规则', content: '甲方有权随时申请退还未消费的预付费用。退费金额按照合同总额减去已消费金额计算。退费申请经公证处审核后，资金将在5个工作日内原路退回至甲方账户。' },
  { title: '违约责任', content: '乙方未按约定提供服务的，甲方有权申请全额退费。乙方出现经营异常（包括但不限于停业、转让、破产等），甲方未消费的预付费用将由公证处全额退还。' },
  { title: '争议解决', content: '因本合同引起的争议，双方应友好协商解决。协商不成的，可向公证处申请调解或向有管辖权的人民法院提起诉讼。' },
  { title: '其他约定', content: '本合同一式三份，甲方、乙方、监管方各执一份，具有同等法律效力。本合同经三方签章后生效，有效期至服务完成或退费完毕之日止。' }
]
</script>

<style lang="scss" scoped>
.contract-viewer { background: #fff; border-radius: 16rpx; }
.contract-scroll { max-height: 70vh; }
.contract-header { padding: 40rpx 30rpx 30rpx; border-bottom: 2rpx solid #1a3c6e; }
.contract-emblem { font-size: 60rpx; display: block; margin-bottom: 16rpx; }
.contract-title { display: block; font-size: 36rpx; font-weight: 700; color: #1a3c6e; margin-bottom: 12rpx; }
.contract-body { padding: 30rpx; }
.contract-section { display: flex; margin-bottom: 16rpx; }
.section-label { color: #666; font-size: 26rpx; width: 240rpx; flex-shrink: 0; }
.section-value { color: #333; font-size: 26rpx; font-weight: 500; }
.amount-text { color: #c41e3a; font-weight: 700; }
.contract-divider { height: 1rpx; background: #e0e0e0; margin: 30rpx 0; }
.contract-clause { margin-bottom: 24rpx; }
.clause-title { display: block; font-size: 26rpx; font-weight: 600; color: #1a3c6e; margin-bottom: 8rpx; }
.clause-content { display: block; font-size: 24rpx; color: #555; line-height: 1.8; text-align: justify; }
.signature-area { padding: 30rpx; border-top: 2rpx dashed #ccc; }
.signature-row { display: flex; justify-content: space-between; }
.signature-block { width: 45%; }
.sig-label { display: block; font-size: 24rpx; color: #666; margin-bottom: 12rpx; text-align: center; }
.sig-box {
  height: 120rpx; border: 2rpx dashed #ccc; border-radius: 8rpx;
  display: flex; align-items: center; justify-content: center;
}
.sig-box.signed { border-color: #1a3c6e; background: rgba(26, 60, 110, 0.03); }
.sig-text { color: #1a3c6e; font-size: 22rpx; font-weight: 600; }
.sig-placeholder { color: #ccc; font-size: 24rpx; }
.notary-seal { border-color: #c41e3a; background: rgba(196, 30, 58, 0.03); border-style: solid; border-width: 3rpx; border-radius: 50%; width: 140rpx; height: 140rpx; }
.seal-text { color: #c41e3a; font-size: 18rpx; font-weight: 700; text-align: center; line-height: 1.4; }
</style>

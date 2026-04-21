<template>
  <view class="container">
    <!-- Product List -->
    <scroll-view scroll-y class="list-scroll">
      <view class="product-item card" v-for="item in products" :key="item.id">
        <view class="product-header">
          <view class="product-icon flex-center">
            <text class="icon-text">{{ item.name[0] }}</text>
          </view>
          <view class="product-info flex-1">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-price">¥{{ item.price }}
              <text class="original" v-if="item.originalPrice > item.price"> / ¥{{ item.originalPrice }}</text>
            </text>
          </view>
          <view class="product-actions">
            <text class="action-edit" @click="editProduct(item)">编辑</text>
            <text class="action-delete" @click="deleteProduct(item)">删除</text>
          </view>
        </view>
        <text class="product-desc text-sm text-gray">{{ item.description }}</text>
        <view class="product-stats">
          <text class="text-sm text-gray">已售{{ item.sales }}件</text>
          <text class="text-sm text-gray" v-if="item.sessions">{{ item.sessions }}次/课程</text>
        </view>
      </view>
      <empty-state v-if="!products.length" text="暂无商品" action-text="添加商品" @action="showForm = true" />
      <view class="bottom-safe-lg"></view>
    </scroll-view>

    <!-- Add Button -->
    <view class="fab" @click="addProduct">
      <text class="fab-icon">+</text>
    </view>

    <!-- Edit Modal -->
    <view class="modal-mask" v-if="showForm" @click.self="showForm = false">
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ editingProduct.id ? '编辑商品' : '添加商品' }}</text>
          <text class="modal-close" @click="showForm = false">&#x2715;</text>
        </view>
        <scroll-view scroll-y class="modal-body">
          <view class="form-group">
            <text class="form-label">商品名称 *</text>
            <input class="form-input" v-model="editingProduct.name" placeholder="请输入商品名称" />
          </view>
          <view class="form-group">
            <text class="form-label">售价 *</text>
            <input class="form-input" v-model="editingProduct.price" type="digit" placeholder="请输入售价" />
          </view>
          <view class="form-group">
            <text class="form-label">原价</text>
            <input class="form-input" v-model="editingProduct.originalPrice" type="digit" placeholder="请输入原价" />
          </view>
          <view class="form-group">
            <text class="form-label">服务次数</text>
            <input class="form-input" v-model="editingProduct.sessions" type="number" placeholder="如为次卡类请填写" />
          </view>
          <view class="form-group">
            <text class="form-label">商品描述</text>
            <textarea class="form-textarea" v-model="editingProduct.description" placeholder="请输入商品描述" />
          </view>
        </scroll-view>
        <view class="modal-footer">
          <button class="btn-outline modal-btn" @click="showForm = false">取消</button>
          <button class="btn-primary modal-btn" @click="saveProduct">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { apiGetMerchantProducts, apiSaveProduct, apiDeleteProduct } from '@/api/index'

const products = ref([])
const showForm = ref(false)
const editingProduct = reactive({
  id: '', name: '', price: '', originalPrice: '', sessions: '', description: ''
})

const loadData = async () => {
  try {
    const res = await apiGetMerchantProducts()
    products.value = res.data.list || []
  } catch (e) { console.error(e) }
}

const addProduct = () => {
  Object.assign(editingProduct, { id: '', name: '', price: '', originalPrice: '', sessions: '', description: '' })
  showForm.value = true
}

const editProduct = (item) => {
  Object.assign(editingProduct, {
    id: item.id, name: item.name,
    price: String(item.price), originalPrice: String(item.originalPrice || ''),
    sessions: String(item.sessions || ''), description: item.description || ''
  })
  showForm.value = true
}

const saveProduct = async () => {
  if (!editingProduct.name) { uni.showToast({ title: '请输入商品名称', icon: 'none' }); return }
  if (!editingProduct.price) { uni.showToast({ title: '请输入售价', icon: 'none' }); return }

  uni.showLoading({ title: '保存中...' })
  try {
    await apiSaveProduct({
      ...editingProduct,
      price: parseFloat(editingProduct.price),
      originalPrice: parseFloat(editingProduct.originalPrice) || parseFloat(editingProduct.price),
      sessions: parseInt(editingProduct.sessions) || 0
    })
    uni.hideLoading()
    uni.showToast({ title: '保存成功', icon: 'success' })
    showForm.value = false
    if (!editingProduct.id) {
      products.value.push({
        ...editingProduct,
        id: 'P' + Date.now(),
        price: parseFloat(editingProduct.price),
        originalPrice: parseFloat(editingProduct.originalPrice) || parseFloat(editingProduct.price),
        sessions: parseInt(editingProduct.sessions) || 0,
        sales: 0
      })
    } else {
      const idx = products.value.findIndex(p => p.id === editingProduct.id)
      if (idx !== -1) {
        products.value[idx] = { ...products.value[idx], ...editingProduct }
      }
    }
  } catch (e) { uni.hideLoading() }
}

const deleteProduct = (item) => {
  uni.showModal({
    title: '确认删除', content: `确定删除"${item.name}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await apiDeleteProduct(item.id)
          products.value = products.value.filter(p => p.id !== item.id)
          uni.showToast({ title: '已删除', icon: 'success' })
        } catch (e) {}
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.list-scroll { height: 100vh; }
.product-header { display: flex; align-items: center; margin-bottom: 12rpx; }
.product-icon {
  width: 72rpx; height: 72rpx; border-radius: 12rpx;
  background: linear-gradient(135deg, #1a3c6e, #2a5298); margin-right: 16rpx;
}
.icon-text { color: #fff; font-size: 28rpx; font-weight: 600; }
.product-name { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 4rpx; }
.product-price { font-size: 28rpx; color: #c41e3a; font-weight: 600; }
.original { font-size: 22rpx; color: #999; text-decoration: line-through; }
.product-actions { display: flex; gap: 20rpx; }
.action-edit { color: #1a3c6e; font-size: 26rpx; }
.action-delete { color: #c41e3a; font-size: 26rpx; }
.product-desc { display: block; margin-bottom: 8rpx; }
.product-stats { display: flex; gap: 20rpx; }

.bottom-safe-lg { height: 140rpx; }

.fab {
  position: fixed; bottom: 60rpx; right: 40rpx;
  width: 100rpx; height: 100rpx; border-radius: 50%;
  background: linear-gradient(135deg, #1a3c6e, #2a5298);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(26,60,110,0.3);
}
.fab-icon { color: #fff; font-size: 48rpx; }

.modal-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex; align-items: flex-end;
  z-index: 999;
}
.modal-content {
  background: #fff; border-radius: 24rpx 24rpx 0 0; width: 100%;
  max-height: 85vh;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 30rpx; border-bottom: 1rpx solid #f0f0f0;
}
.modal-title { font-size: 32rpx; font-weight: 600; color: #333; }
.modal-close { font-size: 32rpx; color: #999; }
.modal-body { padding: 30rpx; max-height: 55vh; }
.form-group { margin-bottom: 24rpx; }
.form-label { display: block; font-size: 28rpx; color: #333; margin-bottom: 10rpx; }
.form-input {
  border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 22rpx; font-size: 28rpx; width: 100%; box-sizing: border-box;
}
.form-textarea {
  width: 100%; height: 160rpx; border: 2rpx solid #e0e0e0; border-radius: 12rpx;
  padding: 16rpx; font-size: 26rpx; box-sizing: border-box;
}
.modal-footer {
  display: flex; gap: 20rpx; padding: 20rpx 30rpx;
  border-top: 1rpx solid #f0f0f0;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.modal-btn { flex: 1; height: 80rpx; line-height: 80rpx; font-size: 28rpx; }
</style>

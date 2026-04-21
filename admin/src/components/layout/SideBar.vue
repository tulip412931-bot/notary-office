<template>
  <div class="sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="sidebar-logo">
      <div class="logo-icon">监</div>
      <h1>资金监管平台</h1>
    </div>
    <div class="sidebar-menu">
      <el-menu :default-active="route.path" router :collapse="appStore.sidebarCollapsed" :collapse-transition="false">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>工作台</span></el-menu-item>
        <el-menu-item index="/merchant"><el-icon><OfficeBuilding /></el-icon><span>商户管理</span></el-menu-item>
        <el-menu-item index="/product"><el-icon><Goods /></el-icon><span>产品管理</span></el-menu-item>
        <el-menu-item index="/order"><el-icon><Document /></el-icon><span>订单管理</span></el-menu-item>
        <el-menu-item index="/fund"><el-icon><Money /></el-icon><span>资金管理</span></el-menu-item>
        <el-menu-item index="/refund"><el-icon><RefreshLeft /></el-icon><span>退款管理</span></el-menu-item>
        <el-menu-item index="/complaint"><el-icon><ChatLineSquare /></el-icon><span>投诉管理</span></el-menu-item>
        <el-menu-item index="/risk"><el-icon><WarnTriangleFilled /></el-icon><span>风险预警</span></el-menu-item>
        <el-menu-item index="/statistics"><el-icon><TrendCharts /></el-icon><span>统计报表</span></el-menu-item>
        <el-menu-item index="/notification"><el-icon><Bell /></el-icon><span>通知中心</span></el-menu-item>
        <el-menu-item index="/system"><el-icon><Setting /></el-icon><span>系统设置</span></el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
const route = useRoute()
const appStore = useAppStore()
</script>

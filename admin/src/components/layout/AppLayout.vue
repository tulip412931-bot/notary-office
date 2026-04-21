<template>
  <div class="admin-layout">
    <SideBar />
    <div class="main-area">
      <TopHeader />
      <div class="page-container">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import SideBar from './SideBar.vue'
import TopHeader from './TopHeader.vue'
</script>

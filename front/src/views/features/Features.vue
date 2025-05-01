<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-28 19:25:47
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-28 19:38:54
 * @FilePath: \front\src\views\features\Features.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="features-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="content-section">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

// 面包屑路径
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '功能详情', path: '/features' }
]);

// 更新时间
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);
</script>

<style scoped>
.features-page {
  min-height: 100vh;
  background-color: #1a1c1e;
}

.main-content {
  display: flex;
  min-height: calc(100vh - 60px);
}

.main {
  flex: 1;
  padding: 20px;
  background-color: #1a1c1e;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 24px;
}
</style> 
<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 23:01:37
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-19 22:04:14
 * @FilePath: \front\src\components\CommonSidebar.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical"
      background-color="#282b30"
      text-color="#8c8c8c"
      active-text-color="#fff"
      :collapse="isCollapse"
      router
    >
      <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>
          <span>{{ item.label }}</span>
        </template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const activeMenu = computed(() => route.path);

const menuItems = ref([
  { path: '/', label: '首页', icon: 'HomeFilled' },
  { path: '/workspace', label: '工作台', icon: 'HomeFilled' },
  { path: '/status-monitoring', label: '状态监控', icon: 'Monitor' },
  { path: '/alarm-management', label: '报警管理', icon: 'Bell' },
  { path: '/shutdown-record', label: '开关机记录', icon: 'Clock' },
  { path: '/system-management', label: '系统管理', icon: 'Setting' }
]);

defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background-color: #282b30;
}

.el-menu-vertical {
  border-right: none;
  height: 100%;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
}

:deep(.el-menu-item.is-active) {
  background-color: #1a1c1e !important;
  border-right: 2px solid var(--el-color-primary);
}

:deep(.el-menu-item:hover) {
  background-color: #1a1c1e !important;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
}
</style>
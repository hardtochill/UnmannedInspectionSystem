<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 23:01:37
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-01 17:18:49
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
      <el-menu-item v-for="item in singleMenuItems" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>
          <span>{{ item.label }}</span>
        </template>
      </el-menu-item>

      <!-- 功能详情子菜单 -->
      <el-sub-menu index="/features">
        <template #title>
          <el-icon><Operation /></el-icon>
          <span>功能详情</span>
        </template>
        <el-menu-item v-for="subItem in featureMenuItems" :key="subItem.path" :index="subItem.path">
          <el-icon><component :is="subItem.icon" /></el-icon>
          <span>{{ subItem.label }}</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { 
  HomeFilled, 
  Monitor, 
  Bell, 
  Clock, 
  Setting,
  Operation,
  PictureFilled
} from '@element-plus/icons-vue';

const route = useRoute();
const activeMenu = computed(() => route.path);

// 单层菜单项
const singleMenuItems = ref([
  { path: '/', label: '首页', icon: 'HomeFilled' },
  { path: '/workspace', label: '工作台', icon: 'HomeFilled' },
  { path: '/status-monitoring', label: '状态监控', icon: 'Monitor' },
  { path: '/alarm-management', label: '报警管理', icon: 'Bell' },
  // { path: '/shutdown-record', label: '开关机记录', icon: 'Clock' },
  { path: '/system-management', label: '系统管理', icon: 'Setting' }
]);

// 功能详情子菜单项
const featureMenuItems = ref([
  { path: '/pipe-leak', label: '管道泄露', icon: 'PictureFilled' },
  { path: '/liquid-level', label: '液面检测', icon: 'PictureFilled' },
  { path: '/digital-meter', label: '数字仪表识别', icon: 'PictureFilled' },
  { path: '/indicator-light', label: '指示灯状态识别', icon: 'PictureFilled' },
  { path: '/liquid-level-recognition', label: '液位识别', icon: 'PictureFilled' },
  { path: '/pointer-meter-recognition', label: '指针表识别', icon: 'PictureFilled' },
  { path: '/switch-recognition', label: '开关识别', icon: 'PictureFilled' },
  { path: '/flow-status-recognition', label: '流水状态识别', icon: 'PictureFilled' }
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

:deep(.el-sub-menu__title) {
  color: #8c8c8c !important;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #1a1c1e !important;
}

:deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #fff !important;
}
</style>
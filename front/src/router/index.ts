/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:39:54
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-21 20:18:38
 * @FilePath: \front\src\router\index.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Home from '@/views/Home.vue';
import Workspace from '@/views/Workspace.vue';
import StatusMonitoring from '@/views/StatusMonitoring.vue';
import AlarmManagement from '@/views/AlarmManagement.vue';
import ShutdownRecord from '@/views/ShutdownRecord.vue';
import SystemManagement from '@/views/SystemManagement.vue';
import Login from '@/views/Login.vue';
import Admin from '@/views/Admin.vue';

interface RouteMeta {
  requiresAuth: boolean;
  [key: string]: unknown;
}

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: '登录',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: '首页',
    component: Home,
    meta: { requiresAuth: true }
  },
  { 
    path: '/workspace', 
    name: '工作台', 
    component: Workspace,
    meta: { requiresAuth: true }
  },
//   { 
//     path: '/status-monitoring', 
//     name: '状态监控', 
//     component: StatusMonitoring,
//     meta: { requiresAuth: true },
//     children: [
//       {
//         path: 'device-detail/:id',
//         name: 'DeviceDetail',
//         component: () => import('@/views/DeviceDetail.vue')
//       },
//       {
//         path: 'device-monitor/:id',
//         name: 'DeviceMonitor',
//         component: () => import('@/views/DeviceMonitor.vue')
//       }
//     ]
//   },
  { 
    path: '/alarm-management', 
    name: '报警管理', 
    component: AlarmManagement,
    meta: { requiresAuth: true }
  },
  { 
    path: '/shutdown-record', 
    name: '停机记录', 
    component: ShutdownRecord,
    meta: { requiresAuth: true }
  },
  { 
    path: '/system-management', 
    name: '系统管理', 
    component: SystemManagement,
    meta: { requiresAuth: true }
  },
  { 
    path: '/admin', 
    name: 'Admin', 
    component: Admin,
    meta: { requiresAuth: true }
  },
  {
    path: '/device-detail/:id',
    name: 'DeviceDetail',
    component: () => import('@/views/DeviceDetail.vue')
  },
  {
    path: '/device-monitor/:id',
    name: 'DeviceMonitor',
    component: () => import('@/views/DeviceMonitor.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/status-monitoring',
    name: '状态监控',
    component: StatusMonitoring,
    meta: { requiresAuth: true }
  },
  {
    path: '/alarm-process/:id',
    name: 'AlarmProcess',
    component: () => import('@/views/AlarmProcess.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: '个人信息',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  }
];

const router = createRouter({ 
  history: createWebHistory(), 
  routes 
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token'); // 检查是否已登录
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && isAuthenticated) {
    next('/home');
  } else {
    next();
  }
});

export default router; 
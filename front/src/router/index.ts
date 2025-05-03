/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-19 23:08:03
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-03 17:33:31
 * @FilePath: \front\src\router\index.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:39:54
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-02 11:08:59
 * @FilePath: \front\src\router\index.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/stores/user';
import Home from '@/views/Home.vue';
import Workspace from '@/views/Workspace.vue';
import StatusMonitoring from '@/views/StatusMonitoring.vue';
import AlarmManagement from '@/views/AlarmManagement.vue';
import ShutdownRecord from '@/views/ShutdownRecord.vue';
import SystemManagement from '@/views/SystemManagement.vue';
import Login from '@/views/Login.vue';
import { ElMessage } from 'element-plus';


const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
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
  { 
    path: '/status-monitoring', 
    name: '状态监控', 
    component: StatusMonitoring,
    meta: { requiresAuth: true }
  },
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
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/device-detail/:id',
    name: '设备详情',
    component: () => import('@/views/DeviceDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/device-monitor/:id',
    name: '设备监管',
    component: () => import('@/views/DeviceMonitor.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/alarm-process/:id',
    name: '报警处理',
    component: () => import('@/views/AlarmProcess.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: '个人信息',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  // 功能详情路由
  {
    path: '/pipe-leak',
    name: '管道泄露检测',
    component: () => import('@/views/features/PipeLeak.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/liquid-level',
    name: '液面检测',
    component: () => import('@/views/features/LiquidLevel.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/digital-meter',
    name: '数字仪表识别',
    component: () => import('@/views/features/DigitalMeter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/indicator-light',
    name: '指示灯状态识别',
    component: () => import('@/views/features/IndicatorLight.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/liquid-level-recognition',
    name: '液位识别',
    component: () => import('@/views/features/LiquidLevelRecognition.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pointer-meter-recognition',
    name: '指针表识别',
    component: () => import('@/views/features/PointerMeterRecognition.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/switch-recognition',
    name: '开关识别',
    component: () => import('@/views/features/SwitchRecognition.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/flow-status-recognition',
    name: '流水状态识别',
    component: () => import('@/views/features/FlowStatusRecognition.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { requiresAuth: false }
  }
];

const router = createRouter({ 
  history: createWebHistory(), 
  routes 
});

// // 路由守卫
// router.beforeEach((to, from, next) => {
//   const userStore = useUserStore();
//   const isAuthenticated = userStore.isLoggedIn();
  
//   // 需要认证但未登录
//   if (to.meta.requiresAuth && !isAuthenticated) {
//     next('/login');
//   } 
//   // 需要管理员权限但不是管理员
//   else if (to.meta.requiresAdmin && !userStore.isAdmin()) {
//     ElMessage.error('您没有管理员权限访问该页面');
//     next('/home');
//   }
//   // 已登录用户访问登录页
//   else if (to.path === '/login' && isAuthenticated) {
//     next('/home');
//   } 
//   // 其他情况正常放行
//   else {
//     next();
//   }
// });

export default router; 
<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 23:01:31
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-30 19:19:23
 * @FilePath: \front\src\components\CommonHeader.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <el-header class="common-header">
    <div class="header-left">
      <h2 class="logo">无人巡检系统</h2>
    </div>
    <div class="header-right">
      <span class="time">{{ currentTime }}</span>

      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-icon><User /></el-icon>
          {{ currentUser?.name || '未登录' }}
          <el-icon><CaretBottom /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <!-- <el-dropdown-item command="logout">退出登录</el-dropdown-item> -->
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <div class="system-icons">
        <!-- 设置按钮 -->
        <el-popover
          placement="bottom-end"
          :width="300"
          trigger="click"
        >
          <template #reference>
            <el-icon class="action-icon"><Setting /></el-icon>
          </template>
          <div class="settings-panel">
            <h3>系统设置</h3>
            <div class="setting-item">
              <span>系统主题</span>
              <el-switch
                v-model="isDarkTheme"
                active-text="深色"
                inactive-text="浅色"
                @change="toggleTheme"
              />
            </div>
            <div class="setting-item">
              <span>消息通知</span>
              <el-switch
                v-model="notificationEnabled"
                @change="toggleNotification"
              />
            </div>
            <div class="setting-item">
              <span>声音提醒</span>
              <el-switch
                v-model="soundEnabled"
                :disabled="!notificationEnabled"
              />
            </div>
            <div class="setting-item">
              <span>自动刷新</span>
              <el-select v-model="refreshInterval" placeholder="选择间隔" size="small">
                <el-option label="关闭" value="0" />
                <el-option label="30秒" value="30" />
                <el-option label="1分钟" value="60" />
                <el-option label="5分钟" value="300" />
              </el-select>
            </div>
          </div>
        </el-popover>

        <!-- 通知按钮 -->
        <el-badge :value="unreadCount" :hidden="!unreadCount">
          <el-popover
            placement="bottom-end"
            :width="350"
            trigger="click"
          >
            <template #reference>
              <el-icon class="action-icon"><Bell /></el-icon>
            </template>
            <div class="notification-panel">
              <div class="notification-header">
                <h3>通知中心</h3>
                <el-button type="primary" link @click="readAllNotifications" v-if="unreadCount">
                  全部已读
                </el-button>
              </div>
              <div class="notification-tabs">
                <el-tabs v-model="activeTab">
                  <el-tab-pane label="报警消息" name="alarm">
                    <div v-if="alarmNotifications.length" class="notification-list">
                      <div
                        v-for="notification in alarmNotifications"
                        :key="notification.id"
                        class="notification-item"
                        :class="{ unread: !notification.read }"
                        @click="handleNotificationClick(notification)"
                      >
                        <el-icon :class="getNotificationIcon(notification.type)">
                          <Warning v-if="notification.type === 'alarm'" />
                          <InfoFilled v-else-if="notification.type === 'info'" />
                        </el-icon>
                        <div class="notification-content">
                          <div class="notification-title">{{ notification.title }}</div>
                          <div class="notification-desc">{{ notification.description }}</div>
                          <div class="notification-time">{{ formatTime(notification.time) }}</div>
                        </div>
                      </div>
                    </div>
                    <div v-else class="no-notification">
                      暂无报警消息
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="系统消息" name="system">
                    <div v-if="systemNotifications.length" class="notification-list">
                      <div
                        v-for="notification in systemNotifications"
                        :key="notification.id"
                        class="notification-item"
                        :class="{ unread: !notification.read }"
                        @click="handleNotificationClick(notification)"
                      >
                        <el-icon :class="getNotificationIcon(notification.type)">
                          <InfoFilled />
                        </el-icon>
                        <div class="notification-content">
                          <div class="notification-title">{{ notification.title }}</div>
                          <div class="notification-desc">{{ notification.description }}</div>
                          <div class="notification-time">{{ formatTime(notification.time) }}</div>
                        </div>
                      </div>
                    </div>
                    <div v-else class="no-notification">
                      暂无系统消息
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
          </el-popover>
        </el-badge>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, CaretBottom, Setting, Bell, Warning, InfoFilled } from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import { accountApi } from '@/api';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const router = useRouter();
const currentTime = ref(new Date().toLocaleString());

// 修改用户信息获取方式
const currentUser = ref(null);
const updateUserInfo = () => {
  const userInfo = JSON.parse(localStorage.getItem('currentUser') || '{}');
  currentUser.value = userInfo;
};

// 系统设置相关
const isDarkTheme = ref(localStorage.getItem('theme') === 'dark');
const notificationEnabled = ref(localStorage.getItem('notificationEnabled') !== 'false');
const soundEnabled = ref(localStorage.getItem('soundEnabled') !== 'false');
const refreshInterval = ref(localStorage.getItem('refreshInterval') || '60');

// 通知相关
const activeTab = ref('alarm');
const alarmNotifications = ref([
  {
    id: 1,
    type: 'alarm',
    title: '设备温度异常',
    description: '设备A1温度超过阈值，请及时处理',
    time: new Date().getTime() - 1000 * 60 * 5,
    read: false
  },
  {
    id: 2,
    type: 'alarm',
    title: '设备离线告警',
    description: '设备B2已离线，请检查网络连接',
    time: new Date().getTime() - 1000 * 60 * 30,
    read: false
  }
]);

const systemNotifications = ref([
  {
    id: 3,
    type: 'info',
    title: '系统维护通知',
    description: '系统将于今晚22:00进行例行维护',
    time: new Date().getTime() - 1000 * 60 * 60,
    read: false
  }
]);

// 计算未读消息数
const unreadCount = computed(() => {
  const alarmUnread = alarmNotifications.value.filter(n => !n.read).length;
  const systemUnread = systemNotifications.value.filter(n => !n.read).length;
  return alarmUnread + systemUnread;
});

// 处理下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile');
      break;
    case 'logout':
      try {
        const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
        console.log('准备登出用户:', currentUser);
        
        if (currentUser.userId) {
          // 先发送登出请求
          const response = await accountApi.logout(currentUser.userId);
          
          if (response.code === 200) {
            ElMessage.success('退出成功');
          } else {
            console.warn('登出请求返回异常:', response);
            ElMessage.warning('登出异常，请重试');
          }
        } else {
          console.warn('未找到用户ID，直接清理存储');
        }
        
        router.push('/login');
      } catch (error) {
        console.error('登出错误:', error);
        ElMessage.error('退出失败，请重试');
        router.push('/login');
      }
      break;
  }
};

// 切换主题
const toggleTheme = (value) => {
  const theme = value ? 'dark' : 'light';
  localStorage.setItem('theme', theme);
  document.documentElement.classList.toggle('dark', value);
  ElMessage.success(`已切换至${theme === 'dark' ? '深色' : '浅色'}主题`);
};

// 切换通知
const toggleNotification = (value) => {
  localStorage.setItem('notificationEnabled', value);
  if (!value) {
    soundEnabled.value = false;
    localStorage.setItem('soundEnabled', false);
  }
  ElMessage.success(`已${value ? '开启' : '关闭'}通知`);
};

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).fromNow();
};

// 获取通知图标样式
const getNotificationIcon = (type) => {
  return {
    'notification-icon': true,
    'alarm-icon': type === 'alarm',
    'info-icon': type === 'info'
  };
};

// 处理通知点击
const handleNotificationClick = (notification) => {
  if (!notification.read) {
    notification.read = true;
    // 这里可以调用后端 API 更新已读状态
  }
  
  // 根据通知类型处理跳转
  if (notification.type === 'alarm') {
    router.push('/alarm-management');
  }
};

// 全部已读
const readAllNotifications = () => {
  alarmNotifications.value.forEach(n => n.read = true);
  systemNotifications.value.forEach(n => n.read = true);
  ElMessage.success('已全部标记为已读');
};

// 监听刷新间隔变化
let refreshTimer;
const startRefreshTimer = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
  
  const interval = Number(refreshInterval.value);
  if (interval > 0) {
    refreshTimer = setInterval(() => {
      // 这里可以添加数据刷新逻辑
      console.log('自动刷新数据...');
    }, interval * 1000);
  }
};

// 在组件挂载时更新用户信息
onMounted(() => {
  // 更新用户信息
  updateUserInfo();
  
  // 更新时间
  setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);

  // 启动自动刷新
  startRefreshTimer();
});
</script>

<style scoped>
.common-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #1a1c1e;
  border-bottom: 1px solid #303030;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  color: #fff;
  font-size: 20px;
  margin: 0;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.time {
  color: #8c8c8c;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.3s ease;
}

.user-info:hover {
  opacity: 0.8;
}

.system-icons {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #8c8c8c;
  font-size: 20px;
}

.system-icons .el-icon {
  cursor: pointer;
  transition: all 0.3s ease;
}

.system-icons .el-icon:hover {
  color: #fff;
  transform: scale(1.1);
}

.action-icon {
  cursor: pointer;
  font-size: 20px;
  transition: all 0.3s ease;
}

.action-icon:hover {
  color: #fff;
  transform: scale(1.1);
}

.settings-panel {
  padding: 16px;
}

.settings-panel h3 {
  margin: 0 0 16px 0;
  color: #fff;
  font-size: 16px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: #fff;
}

.notification-panel {
  padding: 16px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.notification-header h3 {
  margin: 0;
  color: #fff;
  font-size: 16px;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 12px;
  gap: 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background-color: #1a1c1e;
}

.notification-item.unread {
  background-color: rgba(7, 98, 189, 0.1);
}

.notification-icon {
  font-size: 20px;
  margin-top: 2px;
}

.alarm-icon {
  color: #ff4d4f;
}

.info-icon {
  color: #1890ff;
}

.notification-content {
  flex: 1;
}

.notification-title {
  color: #fff;
  font-size: 14px;
  margin-bottom: 4px;
}

.notification-desc {
  color: #8c8c8c;
  font-size: 12px;
  margin-bottom: 4px;
}

.notification-time {
  color: #8c8c8c;
  font-size: 12px;
}

.no-notification {
  text-align: center;
  color: #8c8c8c;
  padding: 40px 0;
}

:deep(.el-tabs__item) {
  color: #8c8c8c !important;
}

:deep(.el-tabs__item.is-active) {
  color: #fff !important;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #303030 !important;
}

:deep(.el-select) {
  width: 100px;
}
</style>
<template>
  <div class="device-detail dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[
          {name: '首页', path: '/'}, 
          {name: fromPath.value === '/alarm-management' ? '报警管理' : '状态监控', 
           path: fromPath.value},
          {name: deviceInfo.name, path: ''}
        ]" />
        
        <div class="content-section">
          <div class="detail-layout">
            <!-- 左侧设备信息 -->
            <div class="device-info">
              <div class="info-header">
                <h2>{{ deviceInfo.name }}</h2>
                <el-tag :type="tagType" size="large">{{ statusText }}</el-tag>
              </div>
              
              <div class="device-image">
                <img :src="deviceInfo.imageUrl || defaultImage" :alt="deviceInfo.name">
              </div>
              
              <div class="info-content">
                <div class="info-item">
                  <span class="label">设备类型：</span>
                  <span class="value">{{ deviceInfo.type }}</span>
                </div>
                <div class="info-item">
                  <span class="label">安装位置：</span>
                  <span class="value">{{ deviceInfo.location }}</span>
                </div>
                <div class="info-item">
                  <span class="label">最后更新：</span>
                  <span class="value">{{ deviceInfo.lastUpdate }}</span>
                </div>
                <div class="info-item">
                  <span class="label">设备描述：</span>
                  <span class="value">{{ deviceInfo.description }}</span>
                </div>
              </div>
            </div>

            <!-- 右侧报警日志 -->
            <div class="alarm-logs">
              <div class="logs-header">
                <h3>报警记录</h3>
                <el-tag type="danger" v-if="activeAlarms.length">
                  {{ activeAlarms.length }} 个未处理报警
                </el-tag>
              </div>

              <div class="logs-content">
                <div v-for="log in activeAlarms" :key="log.id" class="log-item" :class="{'is-active': log.status === 'pending'}">
                  <div class="log-header">
                    <el-tag :type="getAlarmLevelType(log.level)" size="small">
                      {{ getAlarmLevelText(log.level) }}
                    </el-tag>
                    <span class="log-time">{{ log.time }}</span>
                  </div>
                  <div class="log-message">{{ log.message }}</div>
                  <div class="log-actions" v-if="log.status === 'pending'">
                    <el-button type="primary" link @click="handleAlarm(log)">处理报警</el-button>
                  </div>
                </div>

                <el-empty v-if="activeAlarms.length === 0" description="暂无未处理的报警记录" />
              </div>

              <!-- 添加报警历史记录 -->
              <div class="alarm-history" v-if="alarmHistory.length">
                <h4>历史报警记录</h4>
                <el-table :data="alarmHistory" style="width: 100%" size="small">
                  <el-table-column prop="time" label="时间" width="160" />
                  <el-table-column prop="type" label="类型" width="120" />
                  <el-table-column prop="message" label="描述" />
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getStatusType(row.status)" size="small">
                        {{ row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import type { DeviceStatus, AlarmRecord } from '@/api/types';
import { getDeviceList } from '@/api/mock';

const route = useRoute();
const router = useRouter();
const deviceId = route.params.id as string;
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const defaultImage = '/images/devices/default.jpg';

// 设备信息
const deviceInfo = ref<DeviceStatus>({
  id: '',
  name: '',
  status: 'normal',
  location: '',
  lastUpdate: '',
  type: '',
  description: ''
});

// 报警记录
const alarmLogs = ref<AlarmRecord[]>([]);

// 获取未处理的报警记录
const activeAlarms = computed(() => {
  return alarmLogs.value.filter(log => log.status === 'pending');
});

// 状态相关
const statusText = computed(() => {
  const statusMap = {
    alarm: '报警',
    warning: '预警',
    normal: '正常',
    offline: '离线',
    shutdown: '停机'
  };
  return statusMap[deviceInfo.value.status];
});

const tagType = computed(() => {
  const typeMap = {
    alarm: 'danger',
    warning: 'warning',
    normal: 'success',
    offline: 'info',
    shutdown: ''
  };
  return typeMap[deviceInfo.value.status];
});

// 报警等级相关
const getAlarmLevelType = (level: string) => {
  const types: Record<string, string> = {
    error: 'danger',
    warning: 'warning',
    info: 'info'
  };
  return types[level] || 'info';
};

const getAlarmLevelText = (level: string) => {
  const texts: Record<string, string> = {
    error: '严重',
    warning: '警告',
    info: '提示'
  };
  return texts[level] || '未知';
};

// 处理报警
const handleAlarm = (log: AlarmRecord) => {
  router.push(`/alarm-process/${log.id}`);
};

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  const html = document.documentElement;
  if (theme.value === 'dark') {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
};

const alarmHistory = ref([
  {
    id: '1',
    time: '2024-04-19 10:30:00',
    type: '温度异常',
    message: '设备温度超过阈值（85℃）',
    status: '已处理'
  },
  {
    id: '2',
    time: '2024-04-18 15:20:00',
    type: '压力异常',
    message: '设备压力超出正常范围',
    status: '已处理'
  }
]);

// 添加来源路径记录
const fromPath = ref(route.query.from?.toString() || '/status-monitoring');

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    '待处理': 'danger',
    '处理中': 'warning',
    '已处理': 'success'
  };
  return types[status] || 'info';
};

onMounted(async () => {
  // 获取设备信息
  const devices = getDeviceList();
  const device = devices.find(d => d.id === deviceId);
  if (device) {
    deviceInfo.value = device;
  }

  // 模拟获取报警记录
  alarmLogs.value = [
    {
      id: '1',
      deviceId: deviceId,
      deviceName: deviceInfo.value.name,
      level: 'error',
      message: '设备温度超过阈值（85℃）',
      time: '2024-12-04 16:15:00',
      status: 'pending'
    },
    {
      id: '2',
      deviceId: deviceId,
      deviceName: deviceInfo.value.name,
      level: 'warning',
      message: '设备振动异常',
      time: '2024-12-04 16:10:00',
      status: 'pending'
    }
  ];
});
</script>

<style scoped>
.device-detail {
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

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
}

/* 左侧设备信息样式 */
.device-info {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.info-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #fff;
}

.device-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}

.device-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-content {
  display: grid;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #8c8c8c;
  width: 100px;
}

.info-item .value {
  color: #fff;
}

/* 右侧报警日志样式 */
.alarm-logs {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.logs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.logs-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #fff;
}

.logs-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.log-item {
  background-color: #282b30;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #303030;
}

.log-item.is-active {
  border-left: 4px solid #ff4d4f;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.log-time {
  color: #8c8c8c;
  font-size: 12px;
}

.log-message {
  color: #fff;
  margin-bottom: 12px;
}

.log-actions {
  display: flex;
  justify-content: flex-end;
}
</style> 
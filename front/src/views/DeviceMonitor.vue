<template>
  <div class="device-monitor dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[
          {name: '首页', path: '/'}, 
          {name: '状态监控', path: '/status-monitoring'},
          {name: deviceInfo.name + ' - 实时监控', path: ''}
        ]" />
        
        <div class="content-section">
          <div class="monitor-layout">
            <!-- 设备信息头部 -->
            <div class="monitor-header">
              <div class="device-info">
                <h2>{{ deviceInfo.name }}</h2>
                <el-tag :type="tagType" size="large">{{ statusText }}</el-tag>
              </div>
              <div class="monitor-actions">
                <el-button-group>
                  <el-button type="primary" :icon="FullScreen" @click="toggleFullscreen">
                    全屏
                  </el-button>
                  <el-button type="warning" :icon="VideoCamera" @click="toggleStream">
                    {{ isStreaming ? '停止' : '开始' }}监控
                  </el-button>
                </el-button-group>
              </div>
            </div>

            <!-- 视频流显示区域 -->
            <div class="video-container" ref="videoContainer">
              <div v-if="isStreaming" class="stream-wrapper">
                <video ref="videoPlayer" autoplay>
                  <source :src="streamUrl" type="application/x-mpegURL">
                  您的浏览器不支持 HTML5 视频播放。
                </video>
              </div>
              <div v-else class="stream-placeholder">
                <el-icon :size="48"><VideoCamera /></el-icon>
                <p>点击"开始监控"按钮启动视频流</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { FullScreen, VideoCamera } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import type { DeviceStatus } from '@/api/types';
import { getDeviceList } from '@/api/mock';

const route = useRoute();
const deviceId = route.params.id as string;
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const isStreaming = ref(false);
const videoContainer = ref<HTMLElement | null>(null);
const videoPlayer = ref<HTMLVideoElement | null>(null);

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

// 模拟视频流地址（实际项目中应该从后端获取）
const streamUrl = computed(() => {
  return `http://your-streaming-server/live/${deviceId}/index.m3u8`;
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

// 切换主题
const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  const html = document.documentElement;
  if (theme.value === 'dark') {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
};

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    videoContainer.value?.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};

// 切换视频流
const toggleStream = () => {
  isStreaming.value = !isStreaming.value;
  if (!isStreaming.value && videoPlayer.value) {
    videoPlayer.value.pause();
    videoPlayer.value.src = '';
  }
};

// 定时更新时间
let timer: number;
onMounted(async () => {
  // 获取设备信息
  const devices = getDeviceList();
  const device = devices.find(d => d.id === deviceId);
  if (device) {
    deviceInfo.value = device;
  }

  // 更新时间
  timer = window.setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer);
  if (isStreaming.value && videoPlayer.value) {
    videoPlayer.value.pause();
    videoPlayer.value.src = '';
  }
});
</script>

<style scoped>
.device-monitor {
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

.monitor-layout {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #1a1c1e;
  border-radius: 8px;
}

.device-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.device-info h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #fff;
}

.video-container {
  background-color: #1a1c1e;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
}

.stream-wrapper {
  width: 100%;
  height: 100%;
}

.stream-wrapper video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.stream-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #8c8c8c;
}

.stream-placeholder .el-icon {
  margin-bottom: 16px;
}

:deep(.el-button-group) {
  display: flex;
}
</style> 
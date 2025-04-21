<template>
  <div class="home-container">
    <div class="header">
      <div class="logo">无人巡检系统</div>
      <div class="nav-tabs">
        <router-link 
          v-for="(item, index) in navItems" 
          :key="index"
          :to="item.path"
          class="nav-item"
          :class="{ active: currentPath === item.path }"
        >
          {{ item.name }}
        </router-link>
      </div>
      <div class="header-right">
        <span class="time">{{ currentTime }}</span>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userInfo.name || '管理员' }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="dashboard">
      <div class="left-panel">
        <div class="chart-card">
          <div class="card-header">设备类型统计</div>
          <div ref="deviceTypeChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">设备运行状态</div>
          <div ref="deviceStatusChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">设备类型占比</div>
          <div ref="deviceTypePieChart" class="chart-container"></div>
        </div>
      </div>

      <div class="center-panel">
        <div class="status-overview">
          <div class="status-item" :class="'status-' + item.type" v-for="item in statusItems" :key="item.type">
            <div class="status-value">{{ item.value }}%</div>
            <div class="status-label">{{ item.label }}</div>
            <div class="status-count">{{ item.count }}台</div>
          </div>
        </div>
        <div class="map-container">
          <div ref="chinaMapChart" class="china-map"></div>
        </div>
      </div>

      <div class="right-panel">
        <div class="chart-card">
          <div class="card-header">报警统计列表</div>
          <div ref="alarmStatChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">报警对比统计</div>
          <div ref="alarmCompareChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">报警处理趋势</div>
          <div ref="alarmTrendChart" class="chart-container"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import * as echarts from 'echarts';
import 'echarts-gl';
import { User } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';

// Register map component
echarts.registerMap('china', {
  type: 'FeatureCollection',
  features: []
});

const route = useRoute();
const router = useRouter();
const currentPath = ref(route.path);
const currentTime = ref(new Date().toLocaleString());

// 导航项配置
const navItems = [
  { name: '工作台', path: '/workspace' },
  { name: '状态监测', path: '/status-monitoring' },
  { name: '报警管理', path: '/alarm-management' },
  { name: '开停机记录', path: '/shutdown-record' },
  { name: '系统管理', path: '/system-management' }
];

// 状态概览数据
const statusItems = ref([
  { type: 'normal', label: '正常运行', value: 37, count: 35 },
  { type: 'warning', label: '异常设备', value: 10, count: 10 },
  { type: 'stopped', label: '停机设备', value: 35, count: 34 },
  { type: 'offline', label: '断网设备', value: 18, count: 18 }
]);

// 图表引用
const deviceTypeChart = ref(null);
const deviceStatusChart = ref(null);
const deviceTypePieChart = ref(null);
const chinaMapChart = ref(null);
const alarmStatChart = ref(null);
const alarmCompareChart = ref(null);
const alarmTrendChart = ref(null);

// 定时器引用
let timer;

// 用户信息
const userInfo = ref({
  name: localStorage.getItem('userName') || '管理员',
  role: localStorage.getItem('userRole') || 'admin'
});

onMounted(() => {
  // 更新时间
  timer = setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);

  // 初始化图表
  initCharts();
});

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer);
  }
});

const initCharts = () => {
  // 设备类型统计柱状图
  if (deviceTypeChart.value) {
    const chart = echarts.init(deviceTypeChart.value);
    chart.setOption({
      grid: {
        top: '10%',
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['类型A', '类型B', '类型C', '类型D', '类型E'],
        axisLine: {
          lineStyle: { color: '#8c8c8c' }
        }
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: { color: '#8c8c8c' }
        },
        splitLine: {
          lineStyle: { color: 'rgba(140, 140, 140, 0.2)' }
        }
      },
      series: [{
        data: [80, 60, 40, 30, 20],
        type: 'bar',
        barWidth: '30%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }]
    });
  }

  // 中国地图
  if (chinaMapChart.value) {
    const chart = echarts.init(chinaMapChart.value);
    
    // Define major cities with their coordinates
    const cities = [
      { name: '北京', value: [116.4551, 40.2539], data: 100 },
      { name: '上海', value: [121.4648, 31.2891], data: 90 },
      { name: '广州', value: [113.5107, 23.2196], data: 80 },
      { name: '深圳', value: [114.5435, 22.5439], data: 85 },
      { name: '成都', value: [104.0657, 30.6799], data: 75 }
    ];

    chart.setOption({
      backgroundColor: '#282b30',
      title: {
        text: '设备分布',
        left: 'center',
        textStyle: {
          color: '#fff'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}'
      },
      visualMap: {
        min: 0,
        max: 100,
        left: 'left',
        top: 'bottom',
        text: ['高', '低'],
        calculable: true,
        inRange: {
          color: ['#142957', '#0b82c6']
        },
        textStyle: {
          color: '#fff'
        }
      },
      series: [{
        name: '设备分布',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: cities,
        symbolSize: 12,
        label: {
          show: false
        },
        itemStyle: {
          color: '#ff4e50'
        }
      }]
    });

    // Handle window resize
    window.addEventListener('resize', () => {
      chart.resize();
    });
  }

  // 报警趋势图
  if (alarmTrendChart.value) {
    const chart = echarts.init(alarmTrendChart.value);
    chart.setOption({
      grid: {
        top: '10%',
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['2024-07', '2024-09', '2024-11'],
        axisLine: { lineStyle: { color: '#8c8c8c' } }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#8c8c8c' } },
        splitLine: { lineStyle: { color: 'rgba(140, 140, 140, 0.2)' } }
      },
      series: [{
        data: [120, 800, 200],
        type: 'line',
        smooth: true,
        lineStyle: { color: '#ff4e50' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 78, 80, 0.3)' },
            { offset: 1, color: 'rgba(255, 78, 80, 0.1)' }
          ])
        }
      }]
    });
  }

  // 设备类型饼图
  if (deviceTypePieChart.value) {
    const chart = echarts.init(deviceTypePieChart.value);
    chart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        right: '10%',
        top: 'center',
        textStyle: { color: '#8c8c8c' }
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        data: [
          { value: 85, name: '类型A' },
          { value: 5, name: '类型B' },
          { value: 4, name: '类型C' },
          { value: 3, name: '类型D' },
          { value: 3, name: '类型E' }
        ],
        itemStyle: {
          borderColor: '#1a1c1e',
          borderWidth: 2
        },
        label: {
          show: false
        }
      }]
    });
  }
};

// 处理下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile');
      break;
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );
        
        // 清除用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userName');
        localStorage.removeItem('userRole');
        
        // 显示退出成功提示
        ElMessage.success('退出成功');
        
        // 重定向到登录页
        router.push('/login');
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('退出失败');
        }
      }
      break;
  }
};
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #1a1c1e;
  color: #fff;
}

.header {
  height: 60px;
  background-color: #282b30;
  display: flex;
  align-items: center;
  padding: 0 20px;
  justify-content: space-between;
  border-bottom: 1px solid #303030;
}

.logo {
  font-size: 20px;
  font-weight: 500;
}

.nav-tabs {
  display: flex;
  gap: 4px;
}

.nav-item {
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  color: #8c8c8c;
  text-decoration: none;
  position: relative;
}

.nav-item.active {
  color: #fff;
  background-color: #1a1c1e;
}

.nav-item:hover {
  color: #fff;
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
  cursor: pointer;
}

.dashboard {
  display: flex;
  padding: 20px;
  gap: 20px;
  height: calc(100vh - 60px);
}

.left-panel, .right-panel {
  width: 25%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.center-panel {
  width: 50%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  flex: 1;
}

.card-header {
  color: #fff;
  font-size: 16px;
  margin-bottom: 20px;
}

.chart-container {
  height: calc(100% - 40px);
}

.status-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.status-item {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  position: relative;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status-label {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 4px;
}

.status-count {
  font-size: 16px;
}

.status-normal .status-value { color: #52c41a; }
.status-warning .status-value { color: #faad14; }
.status-stopped .status-value { color: #ff4d4f; }
.status-offline .status-value { color: #8c8c8c; }

.map-container {
  flex: 1;
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
}

.china-map {
  height: 100%;
  width: 100%;
}

:deep(.el-dropdown-menu) {
  background-color: #282b30;
  border: 1px solid #303030;
}

:deep(.el-dropdown-menu__item) {
  color: #fff;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #0762bd;
}

/* 添加分割线样式 */
:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #303030;
  margin-top: 4px;
  padding-top: 8px;
}
</style>
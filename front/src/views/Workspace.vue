<template>
  <div class="workspace dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" @open="handleOpen" @close="handleClose" />
      <div class="main">
        <CommonBreadcrumb />
        <div class="summary">
          <div class="summary-card alarm-devices">
            <div class="card-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="card-content">
              <h3>报警设备</h3>
              <div class="main-value">{{ workspaceData.alarmDevices || 10 }} 台</div>
              <div class="sub-value">报警测点 {{ workspaceData.alarmPoints || 19 }} 个</div>
            </div>
          </div>
          <div class="summary-card pending-alarms">
            <div class="card-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="card-content">
              <h3>等待处置</h3>
              <div class="main-value">{{ workspaceData.alarmDisposal?.waiting || 1149 }} 条</div>
              <div class="sub-value">完成处置 {{ workspaceData.alarmDisposal?.completed || 18 }} 条</div>
            </div>
          </div>
          <div class="summary-card monitor-devices">
            <div class="card-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="card-content">
              <h3>监测设备</h3>
              <div class="main-value">{{ workspaceData.deviceInfo?.detected || 98 }} 台</div>
              <div class="sub-value">关注设备 {{ workspaceData.deviceInfo?.followed || 4 }} 台</div>
            </div>
          </div>
          <div class="summary-card device-status">
            <div class="card-icon">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="card-content">
              <h3>停机设备</h3>
              <div class="main-value">{{ workspaceData.operatingStatus?.stopped || 34 }} 台</div>
              <div class="sub-value">断网设备 {{ workspaceData.operatingStatus?.offline || 18 }} 台</div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="section-header">
            <h3>异常数据监控</h3>
            <div class="time-filter">
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button label="today">今日</el-radio-button>
                <el-radio-button label="yesterday">昨日</el-radio-button>
                <el-radio-button label="week">近7天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <el-table 
            :data="workspaceData.pendingAlarms" 
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column type="index" label="序号" width="70"></el-table-column>
            <el-table-column prop="deviceName" label="设备名称"></el-table-column>
            <el-table-column prop="location" label="测点名称"></el-table-column>
            <el-table-column prop="status" label="检测状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '异常' ? 'danger' : 'success'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleDispose(row.id)">处置</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="chart-section">
          <div class="section-header">
            <h3>报警趋势</h3>
          </div>
          <ReusableChart :chartData="chartData" chartType="line" @click="handleChartClick" />
        </div>
      </div>
    </div>

    <!-- 图表详情弹窗 -->
    <el-dialog
      v-model="chartDetailVisible"
      :title="chartDetailTitle"
      width="800px"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <div class="detail-summary">
          <div class="detail-item">
            <span class="label">时间点：</span>
            <span class="value">{{ chartDetail.time }}</span>
          </div>
          <div class="detail-item">
            <span class="label">报警数量：</span>
            <span class="value highlight">{{ chartDetail.value }} 条</span>
          </div>
        </div>

        <el-divider>报警详情</el-divider>

        <el-table
          :data="chartDetail.alarms"
          style="width: 100%"
          class="custom-table"
          v-if="chartDetail.alarms?.length"
        >
          <el-table-column type="index" label="序号" width="70" />
          <el-table-column prop="deviceName" label="设备名称" />
          <el-table-column prop="alarmType" label="报警类型" />
          <el-table-column prop="level" label="报警等级">
            <template #default="{ row }">
              <el-tag :type="getAlarmLevelType(row.level)">
                {{ row.level }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="报警时间" />
          <el-table-column prop="status" label="处理状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div v-else class="no-data">
          暂无报警详情数据
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { Warning, Bell, Monitor, Tools } from '@element-plus/icons-vue';
import { getWorkspaceData } from '@/api/mock';
import ReusableChart from '@/components/ReusableChart.vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const isCollapse = ref(false);
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const timeRange = ref('today');
const workspaceData = ref({});
const loading = ref(false);
const router = useRouter();

// 图表详情相关
const chartDetailVisible = ref(false);
const chartDetailTitle = ref('');
const chartDetail = ref({});
const detailLoading = ref(false);

const handleOpen = (key, keyPath) => {
  console.log(key, keyPath);
};

const handleClose = (key, keyPath) => {
  console.log(key, keyPath);
};

const chartData = ref({
  xAxisData: ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05', '2024-06'],
  yAxisData: [30, 40, 20, 50, 60, 70],
  options: {
    title: {
      text: '报警趋势分析',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05', '2024-06']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [30, 40, 20, 50, 60, 70],
      type: 'line',
      smooth: true,
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#0b82c6'
      },
      itemStyle: {
        color: '#0b82c6',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(11, 130, 198, 0.3)' },
          { offset: 1, color: 'rgba(11, 130, 198, 0.05)' }
        ])
      }
    }]
  }
});

const pieChartData = ref({
  options: {
    title: {
      text: '设备状态分布',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemWidth: 10,
      itemHeight: 10
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 1048, name: '正常', itemStyle: { color: '#52c41a' } },
          { value: 735, name: '告警', itemStyle: { color: '#faad14' } },
          { value: 580, name: '故障', itemStyle: { color: '#ff4d4f' } },
          { value: 484, name: '离线', itemStyle: { color: '#8c8c8c' } }
        ]
      }
    ]
  }
});

const barChartData = ref({
  options: {
    title: {
      text: '设备类型分布',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['类型A', '类型B', '类型C', '类型D', '类型E', '类型F']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [120, 200, 150, 80, 70, 110],
        type: 'bar',
        barWidth: '40%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#1890ff' },
            { offset: 1, color: '#1677ff' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
});

const toggleTheme = () => {
  document.body.classList.toggle('dark-theme', theme.value === 'dark');
};

const handleDispose = (id) => {
  router.push(`/alarm-process/${id}`);
};

// 获取报警等级对应的类型
const getAlarmLevelType = (level) => {
  const types = {
    '一级': 'danger',
    '二级': 'warning',
    '三级': 'info'
  };
  return types[level] || 'info';
};

// 获取状态对应的类型
const getStatusType = (status) => {
  const types = {
    '待处理': 'danger',
    '处理中': 'warning',
    '已处理': 'success'
  };
  return types[status] || 'info';
};

// 模拟获取图表点击节点的详细数据
const fetchChartNodeDetail = async (time, value) => {
  detailLoading.value = true;
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    const alarms = [];
    const alarmTypes = ['温度异常', '压力异常', '振动异常', '速度异常', '位置偏差'];
    const alarmLevels = ['一级', '二级', '三级'];
    const statusList = ['待处理', '处理中', '已处理'];
    
    // 生成随机报警数据
    for (let i = 0; i < value; i++) {
      alarms.push({
        deviceName: `设备${Math.floor(Math.random() * 10) + 1}`,
        alarmType: alarmTypes[Math.floor(Math.random() * alarmTypes.length)],
        level: alarmLevels[Math.floor(Math.random() * alarmLevels.length)],
        time: new Date(time).toLocaleString('zh-CN'),
        status: statusList[Math.floor(Math.random() * statusList.length)]
      });
    }
    
    return {
      time,
      value,
      alarms: alarms.slice(0, 10) // 只显示前10条数据
    };
  } catch (error) {
    ElMessage.error('获取详细数据失败');
    return {
      time,
      value,
      alarms: []
    };
  } finally {
    detailLoading.value = false;
  }
};

// 模拟API调用
const fetchWorkspaceData = async (timeRange) => {
  loading.value = true;
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 根据时间范围生成不同的数据
    const now = new Date();
    let data;
    
    switch (timeRange) {
      case 'today':
        data = generateHourlyData(now, 24);
        break;
      case 'yesterday':
        const yesterday = new Date(now);
        yesterday.setDate(yesterday.getDate() - 1);
        data = generateHourlyData(yesterday, 24);
        break;
      case 'week':
        data = generateDailyData(now, 7);
        break;
      default:
        data = generateHourlyData(now, 24);
    }
    
    return {
      alarmDevices: Math.floor(Math.random() * 20) + 5,
      alarmPoints: Math.floor(Math.random() * 30) + 10,
      alarmDisposal: {
        waiting: Math.floor(Math.random() * 2000) + 1000,
        completed: Math.floor(Math.random() * 50) + 10
      },
      deviceInfo: {
        detected: Math.floor(Math.random() * 50) + 80,
        followed: Math.floor(Math.random() * 10) + 1
      },
      operatingStatus: {
        stopped: Math.floor(Math.random() * 40) + 20,
        offline: Math.floor(Math.random() * 20) + 10
      },
      pendingAlarms: generatePendingAlarms(),
      chartData: data
    };
  } catch (error) {
    ElMessage.error('获取数据失败');
    return {
      alarmDevices: 0,
      alarmPoints: 0,
      alarmDisposal: { waiting: 0, completed: 0 },
      deviceInfo: { detected: 0, followed: 0 },
      operatingStatus: { stopped: 0, offline: 0 },
      pendingAlarms: [],
      chartData: { xAxis: [], series: [] }
    };
  } finally {
    loading.value = false;
  }
};

// 生成小时数据
const generateHourlyData = (startDate, hours) => {
  const data = [];
  const xAxis = [];
  
  for (let i = 0; i < hours; i++) {
    const date = new Date(startDate);
    date.setHours(date.getHours() - (hours - 1 - i));
    xAxis.push(date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }));
    data.push(Math.floor(Math.random() * 50) + 10);
  }
  
  return { xAxis, series: data };
};

// 生成每日数据
const generateDailyData = (startDate, days) => {
  const data = [];
  const xAxis = [];
  
  for (let i = 0; i < days; i++) {
    const date = new Date(startDate);
    date.setDate(date.getDate() - (days - 1 - i));
    xAxis.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }));
    data.push(Math.floor(Math.random() * 100) + 20);
  }
  
  return { xAxis, series: data };
};

// 生成待处理报警数据
const generatePendingAlarms = () => {
  const alarms = [];
  const locations = ['Z轴动力定位效果', 'X轴位移传感器', 'Y轴速度反馈', '主轴温度', '液压压力'];
  
  for (let i = 1; i <= 5; i++) {
    alarms.push({
      id: i,
      deviceName: `模块${i}设备`,
      location: locations[i - 1],
      status: Math.random() > 0.3 ? '异常' : '正常',
      time: new Date().toLocaleString('zh-CN')
    });
  }
  
  return alarms;
};

// 更新图表数据
const updateChartData = (data) => {
  chartData.value = {
    ...chartData.value,
    xAxisData: data.xAxis,
    yAxisData: data.series,
    options: {
      ...chartData.value.options,
      xAxis: {
        ...chartData.value.options.xAxis,
        data: data.xAxis
      },
      series: [{
        ...chartData.value.options.series[0],
        data: data.series
      }]
    }
  };
};

// 处理时间范围变化
const handleTimeRangeChange = async () => {
  const data = await fetchWorkspaceData(timeRange.value);
  workspaceData.value = data;
  updateChartData(data.chartData);
};

// 处理图表点击
const handleChartClick = async (params) => {
  if (params.componentType === 'series') {
    chartDetailTitle.value = '报警详情';
    chartDetailVisible.value = true;
    
    // 获取详细数据
    const detail = await fetchChartNodeDetail(params.name, params.value);
    chartDetail.value = detail;
  }
};

// 监听时间范围变化
watch(timeRange, () => {
  handleTimeRangeChange();
});

onMounted(async () => {
  await handleTimeRangeChange();
});
</script>

<style scoped>
.workspace {
  min-height: 100vh;
  background-color: #1a1c1e;
  color: #fff;
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

.summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.summary-card {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.3s ease;
}

.alarm-devices .card-icon {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.pending-alarms .card-icon {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.monitor-devices .card-icon {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.device-status .card-icon {
  background-color: rgba(47, 84, 235, 0.1);
  color: #2f54eb;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  color: #8c8c8c;
  font-size: 14px;
  margin: 0 0 8px 0;
  font-weight: normal;
}

.main-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}

.sub-value {
  font-size: 12px;
  color: #8c8c8c;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  color: #fff;
  margin: 0;
  font-weight: 500;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
}

.chart-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-summary {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item .label {
  color: #8c8c8c;
}

.detail-item .value {
  color: #fff;
}

.detail-item .value.highlight {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 16px;
}

.no-data {
  text-align: center;
  color: #8c8c8c;
  padding: 40px 0;
}

:deep(.el-dialog) {
  background-color: #282b30;
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-dialog__body) {
  color: #fff;
}

:deep(.el-divider) {
  background-color: #303030;
}

:deep(.el-divider__text) {
  color: #8c8c8c;
  background-color: #282b30;
}

:deep(.el-table) {
  background-color: transparent !important;
}

:deep(.el-table th) {
  background-color: #1a1c1e !important;
  border-bottom: 1px solid #303030;
  color: #8c8c8c;
}

:deep(.el-table td) {
  background-color: transparent !important;
  border-bottom: 1px solid #303030;
  color: #fff;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #1a1c1e !important;
}
</style>
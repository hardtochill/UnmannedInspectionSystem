<template>
  <div class="alarm-management dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '报警管理', path: '/alarm-management'}]" />
        
        <div class="content-section">
          <div class="section-header">
            <div class="filter-section">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="filter-item"
              />
              <el-select v-model="deviceType" placeholder="设备类型" class="filter-item">
                <el-option label="类型1" value="type1" />
                <el-option label="类型2" value="type2" />
              </el-select>
              <el-select v-model="alarmLevel" placeholder="报警等级" class="filter-item">
                <el-option label="一级" value="1" />
                <el-option label="二级" value="2" />
                <el-option label="三级" value="3" />
              </el-select>
              <el-select v-model="status" placeholder="处理状态" class="filter-item">
                <el-option label="待处理" value="pending" />
                <el-option label="处理中" value="processing" />
                <el-option label="已处理" value="completed" />
              </el-select>
              <el-button type="primary" @click="query">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </div>
          </div>

          <div class="status-cards">
            <div class="status-card">
              <div class="card-icon warning">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="card-content">
                <h3>待处理报警</h3>
                <div class="main-value">{{ alarmCount.pending || 156 }}</div>
              </div>
            </div>
            <div class="status-card">
              <div class="card-icon processing">
                <el-icon><Loading /></el-icon>
              </div>
              <div class="card-content">
                <h3>处理中报警</h3>
                <div class="main-value">{{ alarmCount.processing || 23 }}</div>
              </div>
            </div>
            <div class="status-card">
              <div class="card-icon completed">
                <el-icon><CircleCheckFilled /></el-icon>
              </div>
              <div class="card-content">
                <h3>已处理报警</h3>
                <div class="main-value">{{ alarmCount.completed || 892 }}</div>
              </div>
            </div>
          </div>

          <el-table 
            :data="alarmData" 
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="name" label="设备名称" />
            <el-table-column prop="type" label="报警类型" />
            <el-table-column prop="level" label="报警等级">
              <template #default="{ row }">
                <el-tag :type="getAlarmLevelType(row.level)">
                  {{ row.level }}级
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
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleAlarmDetail(row)">详情</el-button>
                <el-button type="primary" link @click="handleProcess(row)" v-if="row.status === '待处理'">
                  处理
                </el-button>
                <el-button type="primary" link @click="viewProcessLog(row)" v-if="row.status === '已处理'">
                  处理记录
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Warning, Loading, CircleCheckFilled } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

const deviceType = ref('');
const alarmLevel = ref('');
const status = ref('');
const dateRange = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(100);
const loading = ref(false);

const alarmCount = ref({
  pending: 156,
  processing: 23,
  completed: 892
});

const alarmData = ref([
  { 
    id: '1',
    deviceId: 'device1',
    name: '设备1', 
    type: '温度异常', 
    level: '1',
    time: '2024-04-19 10:30:00',
    status: '待处理'
  },
  { 
    id: '2',
    deviceId: 'device2',
    name: '设备2', 
    type: '压力异常', 
    level: '2',
    time: '2024-04-19 10:28:00',
    status: '处理中'
  },
  { 
    id: '3',
    deviceId: 'device3',
    name: '设备3', 
    type: '振动异常', 
    level: '3',
    time: '2024-04-19 10:25:00',
    status: '已处理'
  }
]);

const router = useRouter();

const getAlarmLevelType = (level) => {
  const types = {
    '1': 'danger',
    '2': 'warning',
    '3': 'info'
  };
  return types[level] || 'info';
};

const getStatusType = (status) => {
  const types = {
    '待处理': 'danger',
    '处理中': 'warning',
    '已处理': 'success'
  };
  return types[status] || 'info';
};

// 模拟API调用
const fetchAlarmData = async (params) => {
  loading.value = true;
  try {
    // 实际项目中这里应该调用后端API
    // 这里模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据过滤
    let filteredData = [...mockAlarmData];
    
    if (params.deviceType) {
      filteredData = filteredData.filter(item => item.type === params.deviceType);
    }
    
    if (params.alarmLevel) {
      filteredData = filteredData.filter(item => item.level === params.alarmLevel);
    }
    
    if (params.status) {
      filteredData = filteredData.filter(item => item.status === params.status);
    }
    
    if (params.dateRange && params.dateRange.length === 2) {
      const startDate = new Date(params.dateRange[0]).getTime();
      const endDate = new Date(params.dateRange[1]).getTime();
      filteredData = filteredData.filter(item => {
        const itemDate = new Date(item.time).getTime();
        return itemDate >= startDate && itemDate <= endDate;
      });
    }
    
    // 更新总数
    total.value = filteredData.length;
    
    // 分页处理
    const start = (params.currentPage - 1) * params.pageSize;
    const end = start + params.pageSize;
    
    return {
      list: filteredData.slice(start, end),
      total: filteredData.length,
      alarmCount: {
        pending: filteredData.filter(item => item.status === '待处理').length,
        processing: filteredData.filter(item => item.status === '处理中').length,
        completed: filteredData.filter(item => item.status === '已处理').length
      }
    };
  } catch (error) {
    ElMessage.error('获取数据失败');
    return {
      list: [],
      total: 0,
      alarmCount: { pending: 0, processing: 0, completed: 0 }
    };
  } finally {
    loading.value = false;
  }
};

// 模拟数据
const mockAlarmData = [
  { 
    id: '1',
    deviceId: 'device1',
    name: '设备1', 
    type: '温度异常', 
    level: '1',
    time: '2024-04-19 10:30:00',
    status: '待处理'
  },
  // ... 添加更多模拟数据
];

// 查询方法
const query = async () => {
  const params = {
    deviceType: deviceType.value,
    alarmLevel: alarmLevel.value,
    status: status.value,
    dateRange: dateRange.value,
    currentPage: currentPage.value,
    pageSize: pageSize.value
  };
  
  const { list, total: totalCount, alarmCount: counts } = await fetchAlarmData(params);
  alarmData.value = list;
  total.value = totalCount;
  alarmCount.value = counts;
};

// 重置方法
const reset = () => {
  deviceType.value = '';
  alarmLevel.value = '';
  status.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  query();
};

// 分页方法
const handleSizeChange = (val) => {
  pageSize.value = val;
  query();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  query();
};

// 初始化加载
onMounted(() => {
  query();
});

const handleAlarmDetail = (row) => {
  router.push({
    path: `/device-detail/${row.deviceId}`,
    query: { from: '/alarm-management' }
  });
};

const handleProcess = (row) => {
  router.push(`/alarm-process/${row.id}`);
};

const viewProcessLog = (row) => {
  console.log('查看处理记录:', row);
};
</script>

<style scoped>
.alarm-management {
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
  padding: 20px;
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
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-item {
  width: 180px;
}

.status-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.status-card {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.card-icon.warning {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.card-icon.processing {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.card-icon.completed {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.card-content h3 {
  color: #8c8c8c;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.main-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.custom-table {
  margin-bottom: 20px;
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #8c8c8c;
  --el-pagination-button-color: #fff;
  --el-pagination-button-bg-color: #1a1c1e;
  --el-pagination-button-disabled-color: #606266;
  --el-pagination-button-disabled-bg-color: #282b30;
  --el-pagination-hover-color: var(--el-color-primary);
}
</style>
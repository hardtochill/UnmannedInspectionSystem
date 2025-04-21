<template>
  <div class="shutdown-record dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '开关机记录', path: '/shutdown-record'}]" />
        
        <div class="content-section">
          <div class="section-header">
            <h3>开关机记录</h3>
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
              <el-select v-model="status" placeholder="运行状态" class="filter-item">
                <el-option label="运行中" value="running" />
                <el-option label="已停机" value="stopped" />
              </el-select>
              <el-button type="primary" @click="query">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </div>
          </div>

          <div class="status-cards">
            <div class="status-card">
              <div class="card-icon running">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <div class="card-content">
                <h3>运行设备</h3>
                <div class="main-value">{{ statusCount.running || 42 }}</div>
              </div>
            </div>
            <div class="status-card">
              <div class="card-icon stopped">
                <el-icon><VideoPause /></el-icon>
              </div>
              <div class="card-content">
                <h3>停机设备</h3>
                <div class="main-value">{{ statusCount.stopped || 8 }}</div>
              </div>
            </div>
          </div>

          <el-table 
            :data="recordData" 
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="name" label="设备名称" />
            <el-table-column prop="location" label="安装位置" />
            <el-table-column prop="status" label="运行状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '运行中' ? 'success' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开机时间" />
            <el-table-column prop="duration" label="运行时长" />
            <!-- <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
              </template>
            </el-table-column> -->
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
import { VideoPlay, VideoPause } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { ElMessage } from 'element-plus';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

const deviceType = ref('');
const status = ref('');
const dateRange = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(100);
const loading = ref(false);

const statusCount = ref({
  running: 42,
  stopped: 8
});

const recordData = ref([
  { 
    name: '设备1', 
    location: '车间A区', 
    status: '运行中', 
    startTime: '2024-04-19 08:00:00',
    duration: '2小时30分钟'
  },
  { 
    name: '设备2', 
    location: '车间B区', 
    status: '已停机', 
    startTime: '2024-04-19 09:00:00',
    duration: '1小时45分钟'
  }
]);

// 模拟API调用
const fetchShutdownData = async (params) => {
  loading.value = true;
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据过滤
    let filteredData = [...mockShutdownData];
    
    if (params.deviceType) {
      filteredData = filteredData.filter(item => item.type === params.deviceType);
    }
    
    if (params.status) {
      filteredData = filteredData.filter(item => item.status === params.status);
    }
    
    if (params.dateRange && params.dateRange.length === 2) {
      const startDate = new Date(params.dateRange[0]).getTime();
      const endDate = new Date(params.dateRange[1]).getTime();
      filteredData = filteredData.filter(item => {
        const itemDate = new Date(item.startTime).getTime();
        return itemDate >= startDate && itemDate <= endDate;
      });
    }
    
    // 更新总数
    total.value = filteredData.length;
    
    // 分页处理
    const start = (params.currentPage - 1) * params.pageSize;
    const end = start + params.pageSize;
    
    // 计算运行和停机数量
    const statusCount = {
      running: filteredData.filter(item => item.status === '运行中').length,
      stopped: filteredData.filter(item => item.status === '已停机').length
    };
    
    return {
      list: filteredData.slice(start, end),
      total: filteredData.length,
      statusCount
    };
  } catch (error) {
    ElMessage.error('获取数据失败');
    return {
      list: [],
      total: 0,
      statusCount: { running: 0, stopped: 0 }
    };
  } finally {
    loading.value = false;
  }
};

// 模拟数据
const mockShutdownData = [
  { 
    id: 1,
    name: '设备1', 
    type: 'type1',
    location: '车间A区', 
    status: '运行中', 
    startTime: '2024-04-19 08:00:00',
    duration: '2小时30分钟'
  },
  { 
    id: 2,
    name: '设备2', 
    type: 'type2',
    location: '车间B区', 
    status: '已停机', 
    startTime: '2024-04-19 09:00:00',
    duration: '1小时45分钟'
  }
  // ... 可以添加更多模拟数据
];

// 查询方法
const query = async () => {
  const params = {
    deviceType: deviceType.value,
    status: status.value,
    dateRange: dateRange.value,
    currentPage: currentPage.value,
    pageSize: pageSize.value
  };
  
  const { list, total: totalCount, statusCount: counts } = await fetchShutdownData(params);
  recordData.value = list;
  total.value = totalCount;
  statusCount.value = counts;
};

// 重置方法
const reset = () => {
  deviceType.value = '';
  status.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  query();
};

const viewDetail = (row) => {
  console.log('查看详情:', row);
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
</script>

<style scoped>
.shutdown-record {
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
  grid-template-columns: repeat(2, 1fr);
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

.card-icon.running {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.card-icon.stopped {
  background-color: rgba(140, 140, 140, 0.1);
  color: #8c8c8c;
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
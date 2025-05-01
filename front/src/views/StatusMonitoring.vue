<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:49:49
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-29 17:59:52
 * @FilePath: \front\src\views\StatusMonitoring.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="status-monitoring dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '状态监控', path: '/status-monitoring'}]" />
        
        <div class="content-section">
          <div class="page-header">
            <h2>设备状态</h2>
            <div class="header-actions">
              <el-select v-model="formWorkshop" placeholder="车间" clearable>
                <el-option label="全部" value="" />
                <el-option label="车间A" value="车间A" />
                <el-option label="车间B" value="车间B" />
                <el-option label="车间C" value="车间C" />
              </el-select>
              <el-select v-model="formDeviceType" placeholder="设备类型" clearable>
                <el-option label="全部" value="" />
                <el-option label="水泵" value="水泵" />
                <el-option label="联轴器" value="联轴器" />
                <el-option label="EDI装置" value="EDI装置" />
              </el-select>
              <el-select v-model="formStatusFilter" placeholder="状态筛选" clearable>
                <el-option label="全部" value="" />
                <el-option label="报警" value="alarm" />
                <el-option label="预警" value="warning" />
                <el-option label="正常" value="normal" />
                <el-option label="离线" value="offline" />
                <el-option label="停机" value="shutdown" />
              </el-select>
              <el-input
                v-model="formSearchKeyword"
                placeholder="搜索设备名称"
                clearable
                class="search-input"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </div>

          <div class="device-grid">
            <DeviceStatusCard
              v-for="device in filteredDevices"
              :key="device.id"
              :device="device"
            />
          </div>

          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[12, 24, 36, 48]"
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

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import DeviceStatusCard from '@/components/DeviceStatusCard.vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { getDeviceList } from '@/api/mock';
import type { DeviceStatus } from '@/api/types';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const deviceList = ref<DeviceStatus[]>([]);
const formWorkshop = ref('');
const formDeviceType = ref('');
const formStatusFilter = ref('');
const formSearchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);
const workshop = ref('');
const deviceType = ref('');
const statusFilter = ref('');
const searchKeyword = ref('');

// 过滤设备列表
const filteredDevices = computed(() => {
  let filtered = deviceList.value;

  if (workshop.value) {
    filtered = filtered.filter(device => device.workshop === workshop.value);
  }
  if (deviceType.value) {
    filtered = filtered.filter(device => device.type === deviceType.value);
  }
  if (statusFilter.value) {
    filtered = filtered.filter(device => device.status === statusFilter.value);
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(device => 
      device.name.toLowerCase().includes(keyword) ||
      device.description?.toLowerCase().includes(keyword)
    );
  }
  total.value = filtered.length;
  // 分页
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filtered.slice(start, end);
});

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  const html = document.documentElement;
  if (theme.value === 'dark') {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};

const handleQuery = () => {
  workshop.value = formWorkshop.value;
  deviceType.value = formDeviceType.value;
  statusFilter.value = formStatusFilter.value;
  searchKeyword.value = formSearchKeyword.value;
  currentPage.value = 1;
};

const handleReset = () => {
  formWorkshop.value = '';
  formDeviceType.value = '';
  formStatusFilter.value = '';
  formSearchKeyword.value = '';
  handleQuery();
};

onMounted(async () => {
  deviceList.value = getDeviceList();
});
</script>

<style scoped>
.status-monitoring {
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
  margin-bottom: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
  color: #fff;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.search-input {
  width: 240px;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

:deep(.el-select) {
  width: 160px;
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
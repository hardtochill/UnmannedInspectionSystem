<template>
  <BaseFeature
    title="指示灯状态识别"
    description="基于计算机视觉的指示灯状态识别系统，可以自动识别和判断设备上指示灯的状态。支持多种类型的指示灯，包括但不限于运行指示灯、警告指示灯、状态指示灯等。系统可以在不同光照条件下准确识别指示灯的颜色和状态。"
    feature-key="indicator-light"
  >
    <template #result="{ result }">
      <div class="light-result">
        <div class="indicators-grid">
          <div 
            v-for="(indicator, index) in result.data.indicators" 
            :key="index"
            class="indicator-card"
          >
            <div class="indicator-header">
              <span class="indicator-name">{{ indicator.name }}</span>
              <div 
                class="indicator-dot"
                :style="{ backgroundColor: getLightColor(indicator.color) }"
              ></div>
            </div>
            <div class="indicator-content">
              <div class="info-item">
                <span class="label">状态：</span>
                <el-tag :type="getStatusType(indicator.status)">
                  {{ getStatusText(indicator.status) }}
                </el-tag>
              </div>
              <div class="info-item">
                <span class="label">颜色：</span>
                <span class="value">{{ getColorText(indicator.color) }}</span>
              </div>
              <div class="info-item">
                <span class="label">亮度：</span>
                <el-progress 
                  :percentage="indicator.brightness * 100"
                  :format="percentageFormat"
                  :color="getLightColor(indicator.color)"
                />
              </div>
              <div class="info-item">
                <span class="label">置信度：</span>
                <span class="value">{{ (indicator.confidence * 100).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="summary-section">
          <h3>检测总结</h3>
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">检测到的指示灯：</span>
              <span class="value">{{ result.data.indicators.length }} 个</span>
            </div>
            <div class="summary-item">
              <span class="label">异常指示灯：</span>
              <span class="value highlight">
                {{ result.data.indicators.filter(i => i.status !== 'normal').length }} 个
              </span>
            </div>
            <div class="summary-item">
              <span class="label">建议：</span>
              <span class="value">{{ result.data.suggestion || '暂无建议' }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </BaseFeature>
</template>

<script setup lang="ts">
import BaseFeature from './BaseFeature.vue';

// 获取指示灯颜色
const getLightColor = (color: string) => {
  const colorMap: Record<string, string> = {
    red: '#F56C6C',
    green: '#67C23A',
    yellow: '#E6A23C',
    blue: '#409EFF',
    white: '#FFFFFF',
    off: '#909399'
  };
  return colorMap[color] || '#909399';
};

// 获取颜色文本
const getColorText = (color: string) => {
  const textMap: Record<string, string> = {
    red: '红色',
    green: '绿色',
    yellow: '黄色',
    blue: '蓝色',
    white: '白色',
    off: '熄灭'
  };
  return textMap[color] || '未知';
};

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: 'success',
    warning: 'warning',
    error: 'danger',
    off: 'info'
  };
  return typeMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: '正常',
    warning: '警告',
    error: '错误',
    off: '关闭'
  };
  return textMap[status] || '未知';
};

// 格式化百分比
const percentageFormat = (percentage: number) => {
  return percentage.toFixed(0) + '%';
};
</script>

<style scoped>
.light-result {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.indicators-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.indicator-card {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 16px;
}

.indicator-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.indicator-name {
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 8px currentColor;
}

.indicator-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  color: #8c8c8c;
  min-width: 60px;
}

.value {
  color: #fff;
}

:deep(.el-progress) {
  flex: 1;
}

:deep(.el-progress-bar__outer) {
  background-color: #282b30 !important;
}

.summary-section {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 20px;
}

.summary-section h3 {
  color: #fff;
  margin: 0 0 16px 0;
  font-size: 16px;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-item .label {
  min-width: 120px;
}

.value.highlight {
  color: var(--el-color-danger);
  font-weight: 500;
}
</style> 
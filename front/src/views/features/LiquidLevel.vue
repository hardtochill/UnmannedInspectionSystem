<template>
  <BaseFeature
    title="液面检测"
    description="基于计算机视觉的液面检测系统，可以准确识别容器中的液面高度，支持多种类型的容器和液体。系统可以实时监测液位变化，并提供精确的液位数据。"
    feature-key="liquid-level"
  >
    <template #result="{ result }">
      <div class="level-result">
        <div class="result-item">
          <span class="label">液面高度：</span>
          <span class="value">{{ result.data.level }}%</span>
          <el-progress 
            :percentage="result.data.level" 
            :color="getLevelColor(result.data.level)"
            :status="getLevelStatus(result.data.level)"
          />
        </div>
        <div class="result-item">
          <span class="label">容器类型：</span>
          <span class="value">{{ result.data.containerType || '未识别' }}</span>
        </div>
        <div class="result-item">
          <span class="label">液体类型：</span>
          <span class="value">{{ result.data.liquidType || '未识别' }}</span>
        </div>
        <div class="result-item">
          <span class="label">置信度：</span>
          <span class="value">{{ (result.data.confidence * 100).toFixed(2) }}%</span>
        </div>
        <div class="result-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(result.data.status)">
            {{ getStatusText(result.data.status) }}
          </el-tag>
        </div>
      </div>
    </template>
  </BaseFeature>
</template>

<script setup lang="ts">
import BaseFeature from './BaseFeature.vue';

// 获取液位颜色
const getLevelColor = (level: number) => {
  if (level >= 90) return '#F56C6C';
  if (level >= 70) return '#E6A23C';
  if (level >= 30) return '#67C23A';
  return '#909399';
};

// 获取液位状态
const getLevelStatus = (level: number) => {
  if (level >= 90) return 'exception';
  if (level >= 70) return 'warning';
  return 'success';
};

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: 'success',
    warning: 'warning',
    alarm: 'danger',
    unknown: 'info'
  };
  return typeMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: '正常',
    warning: '警告',
    alarm: '报警',
    unknown: '未知'
  };
  return textMap[status] || '未知';
};
</script>

<style scoped>
.level-result {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.label {
  color: #8c8c8c;
  min-width: 80px;
}

.value {
  color: #fff;
  min-width: 60px;
}

:deep(.el-progress) {
  flex: 1;
  margin-left: 16px;
}

:deep(.el-progress-bar__outer) {
  background-color: #1a1c1e !important;
}
</style> 
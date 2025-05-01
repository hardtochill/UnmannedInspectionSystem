<template>
  <BaseFeature
    title="数字仪表识别"
    description="基于深度学习的数字仪表识别系统，可以自动识别和读取各类数字仪表显示的数值。支持多种类型的数字仪表，包括但不限于压力表、温度计、电表等。系统具有高精度的字符识别能力，可在不同光照条件下准确读取数值。"
    feature-key="digital-meter"
  >
    <template #result="{ result }">
      <div class="meter-result">
        <div class="result-item">
          <span class="label">仪表类型：</span>
          <span class="value">{{ result.data.meterType || '未识别' }}</span>
        </div>
        <div class="result-item">
          <span class="label">读数：</span>
          <span class="value highlight">{{ result.data.reading || '未识别' }}</span>
          <span class="unit">{{ result.data.unit }}</span>
        </div>
        <div class="result-item">
          <span class="label">置信度：</span>
          <el-progress 
            :percentage="result.data.confidence * 100" 
            :format="percentageFormat"
            :status="getConfidenceStatus(result.data.confidence)"
          />
        </div>
        <div class="result-item">
          <span class="label">状态：</span>
          <el-tag :type="getReadingStatus(result.data.reading, result.data.normalRange)">
            {{ getStatusText(result.data.reading, result.data.normalRange) }}
          </el-tag>
        </div>
        <div class="result-item" v-if="result.data.normalRange">
          <span class="label">正常范围：</span>
          <span class="value">{{ formatRange(result.data.normalRange) }}</span>
        </div>
      </div>
    </template>
  </BaseFeature>
</template>

<script setup lang="ts">
import BaseFeature from './BaseFeature.vue';

interface Range {
  min: number;
  max: number;
}

// 格式化百分比
const percentageFormat = (percentage: number) => {
  return percentage.toFixed(1) + '%';
};

// 获取置信度状态
const getConfidenceStatus = (confidence: number) => {
  if (confidence >= 0.9) return 'success';
  if (confidence >= 0.7) return 'warning';
  return 'exception';
};

// 获取读数状态
const getReadingStatus = (reading: number, range: Range | null) => {
  if (!range || !reading) return 'info';
  if (reading < range.min) return 'danger';
  if (reading > range.max) return 'danger';
  return 'success';
};

// 获取状态文本
const getStatusText = (reading: number, range: Range | null) => {
  if (!range || !reading) return '未知';
  if (reading < range.min) return '低于正常值';
  if (reading > range.max) return '高于正常值';
  return '正常';
};

// 格式化范围
const formatRange = (range: Range) => {
  return `${range.min} - ${range.max}`;
};
</script>

<style scoped>
.meter-result {
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
}

.value.highlight {
  font-size: 24px;
  font-weight: 500;
  color: var(--el-color-primary);
}

.unit {
  color: #8c8c8c;
  margin-left: 4px;
}

:deep(.el-progress) {
  flex: 1;
}

:deep(.el-progress-bar__outer) {
  background-color: #1a1c1e !important;
}
</style> 
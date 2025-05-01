<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-28 15:41:30
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-29 17:53:37
 * @FilePath: \front\src\views\features\PipeLeak.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <BaseFeature
    title="管道泄露检测"
    description="基于深度学习的管道泄露检测系统，可以自动识别图像中的管道泄露位置，并提供泄露程度的评估。支持多种工业场景下的管道泄露检测，包括但不限于水管、油管、气管等。"
    feature-key="pipe-leak"
  >
    <template #result="{ result }">
      <div class="leak-result">
        <div class="result-item">
          <span class="label">泄露位置：</span>
          <span class="value">{{ result.data.location || '未检测到泄露' }}</span>
        </div>
        <div class="result-item">
          <span class="label">泄露程度：</span>
          <el-tag :type="getLeakLevelType(result.data.level)">
            {{ getLeakLevelText(result.data.level) }}
          </el-tag>
        </div>
        <div class="result-item">
          <span class="label">置信度：</span>
          <span class="value">{{ (result.data.confidence * 100).toFixed(2) }}%</span>
        </div>
        <div class="result-item">
          <span class="label">建议：</span>
          <span class="value">{{ result.data.suggestion || '暂无建议' }}</span>
        </div>
      </div>
    </template>
  </BaseFeature>
</template>

<script setup lang="ts">
import BaseFeature from './BaseFeature.vue';

// 获取泄露等级类型
const getLeakLevelType = (level: string) => {
  const typeMap: Record<string, string> = {
    severe: 'danger',
    moderate: 'warning',
    slight: 'info',
    none: 'success'
  };
  return typeMap[level] || 'info';
};

// 获取泄露等级文本
const getLeakLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    severe: '严重',
    moderate: '中等',
    slight: '轻微',
    none: '正常'
  };
  return textMap[level] || '未知';
};
</script>

<style scoped>
.leak-result {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  color: #8c8c8c;
  min-width: 80px;
}

.value {
  color: #fff;
}
</style> 
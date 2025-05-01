<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-29 17:49:32
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-29 17:50:10
 * @FilePath: \front\src\views\features\LiquidLevelRecognition.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <BaseFeature
    title="液位识别"
    description="基于图像识别的液位检测系统，可自动识别容器或管道中的液位高度，适用于多种液体监测场景。支持实时报警和历史数据分析。"
    feature-key="liquid-level-recognition"
  >
    <template #result="{ result }">
      <div class="feature-result">
        <div class="result-item">
          <span class="label">液位高度：</span>
          <span class="value">{{ result.data.level || '未检测到液位' }}</span>
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
</script>

<style scoped>
.feature-result {
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
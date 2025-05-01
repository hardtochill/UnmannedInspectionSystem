<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-29 17:49:46
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-29 17:54:02
 * @FilePath: \front\src\views\features\PointerMeterRecognition.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <BaseFeature
    title="指针表识别"
    description="基于图像识别的指针表读数系统，可自动识别各类机械指针表的读数，适用于压力表、电流表等多种工业仪表。支持异常报警和数据存档。"
    feature-key="pointer-meter-recognition"
  >
    <template #result="{ result }">
      <div class="feature-result">
        <div class="result-item">
          <span class="label">表盘读数：</span>
          <span class="value">{{ result.data.value || '未检测到读数' }}</span>
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
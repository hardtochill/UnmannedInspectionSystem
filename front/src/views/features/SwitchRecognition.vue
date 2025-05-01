<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-29 17:50:06
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-29 17:50:35
 * @FilePath: \front\src\views\features\SwitchRecognition.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <BaseFeature
    title="开关识别"
    description="基于图像识别的开关状态检测系统，可自动识别各类工业开关的开/关状态，适用于电气柜、控制面板等场景。支持异常报警和远程监控。"
    feature-key="switch-recognition"
  >
    <template #result="{ result }">
      <div class="feature-result">
        <div class="result-item">
          <span class="label">开关状态：</span>
          <span class="value">{{ result.data.state || '未检测到状态' }}</span>
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
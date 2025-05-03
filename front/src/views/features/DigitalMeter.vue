<template>
  <BaseFeature
    title="数字仪表识别"
    description="基于深度学习的数字仪表识别系统，可以自动识别和读取各类数字仪表显示的数值。支持多种类型的数字仪表，包括但不限于压力表、温度计、电表等。系统具有高精度的字符识别能力，可在不同光照条件下准确读取数值。"
    feature-key="digital-meter"
    @detect="handleDetection"
  >
    <template #result="{ result }">
      <div class="feature-result">
        <div v-if="isLoading" class="loading-container">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <p>正在处理图片，请稍候...</p>
        </div>
        <div v-else>
          <div v-if="resultImage" class="result-image-container">
            <h4>处理结果图片：</h4>
            <img :src="resultImage" class="result-image" alt="处理结果" />
          </div>
        </div>
      </div>
    </template>
  </BaseFeature>
</template>

<script setup lang="ts">
import { defineEmits, ref } from 'vue';
import BaseFeature from './BaseFeature.vue';
import { detectApi } from '@/api';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { Loading } from '@element-plus/icons-vue';

const isLoading = ref(false);
const resultImage = ref('');

const handleDetection = async (file: File) => {
  try {
    // console.log('收到文件对象:', file);
    isLoading.value = true;
    resultImage.value = '';
    
    // 确保文件对象有效
    if (!file || !(file instanceof File)) {
      // console.error('无效的文件对象:', file);
      ElMessage.error('无效的文件对象');
      isLoading.value = false;
      return {
        status: 'error',
        message: '无效的文件对象',
        data: {}
      };
    }
    
    
    // 创建FormData对象来发送文件
    const formData = new FormData();
    
    // 使用image作为字段名添加文件
    formData.append('image', file, file.name);
    
    // 打印FormData内容（用于调试）
    // console.log('FormData已创建，字段名为image');
    
    // 直接使用detectApi发送请求
    const result = await detectApi.detect(formData);
    
    // console.log('检测结果:', result);
    
    // 处理返回的base64图像
    if (result.code === 200 && result.data) {
      // 确保base64字符串格式正确
      const base64Image = result.data.startsWith('data:image') 
        ? result.data 
        : `data:image/jpeg;base64,${result.data}`;
      
      resultImage.value = base64Image;
      
      return {
        status: 'success',
        message: '检测完成',
        data: result.data
      };
    } else {
      ElMessage.error(result.info || '检测失败');
      return {
        status: 'error',
        message: result.info || '检测失败',
        data: {}
      };
    }
  } catch (error) {
    // console.error('液位检测失败:', error);
    ElMessage.error('检测过程中发生错误: ' + (error instanceof Error ? error.message : '未知错误'));
    return {
      status: 'error',
      message: '检测过程中发生错误',
      data: {}
    };
  } finally {
    isLoading.value = false;
  }
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
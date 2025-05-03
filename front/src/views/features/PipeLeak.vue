<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-28 15:41:30
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-03 17:02:46
 * @FilePath: \front\src\views\features\PipeLeak.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <BaseFeature
    title="管道泄露检测"
    description="基于深度学习的管道泄露检测系统，可以自动识别图像中的管道泄露位置，并提供泄露程度的评估。支持多种工业场景下的管道泄露检测，包括但不限于水管、油管、气管等。"
    feature-key="pipe-leak"
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
    const result = await detectApi.detect(formData, 'best');
    
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
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}
.loading-icon {
  font-size: 48px;
  color: #409eff;
  animation: rotate 2s linear infinite;
}
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.loading-container p {
  margin-top: 16px;
  color: #8c8c8c;
}
.result-image-container {
  margin-bottom: 20px;
}
.result-image-container h4 {
  color: #fff;
  margin-bottom: 10px;
}
.result-image {
  max-width: 100%;
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}
</style> 
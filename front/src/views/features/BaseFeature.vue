<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        
        <div class="content-section">
          <!-- 功能介绍部分 -->
          <div class="feature-intro">
            <h2>{{ title }}</h2>
            <p class="description">{{ description }}</p>
          </div>

          <!-- 功能展示部分 -->
          <div class="feature-demo">
            <div class="upload-section">
              <div class="preview-container" v-if="imageUrl">
                <img :src="imageUrl" class="preview-image" />
                <div class="image-actions">
                  <el-button type="danger" @click="handleRemove">删除图片</el-button>
                  <el-button type="primary" @click="handleDetect" :loading="detecting">
                    开始检测
                  </el-button>
                </div>
              </div>
              <el-upload
                v-else
                class="upload-area"
                drag
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleChange"
                accept="image/*"
              >
                <el-icon class="upload-icon"><Upload /></el-icon>
                <div class="upload-text">
                  <span>将图片拖到此处，或<em>点击上传</em></span>
                  <p>支持 jpg、png 格式图片</p>
                </div>
              </el-upload>
            </div>

            <!-- 检测结果展示 -->
            <div v-if="result" class="result-section">
              <h3>检测结果</h3>
              <slot name="result" :result="result">
                <!-- 默认结果展示 -->
                <pre class="result-content">{{ JSON.stringify(result, null, 2) }}</pre>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Upload } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const props = defineProps<{
  title: string;
  description: string;
  featureKey: string;
}>();

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const imageUrl = ref('');
const detecting = ref(false);
const result = ref(null);

// 面包屑路径
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '功能详情', path: '/features' },
  { name: props.title, path: '' }
]);

// 处理文件改变
const handleChange = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    return;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！');
    return;
  }

  // 创建预览URL
  imageUrl.value = URL.createObjectURL(file.raw);
};

// 移除图片
const handleRemove = () => {
  imageUrl.value = '';
  result.value = null;
};

// 开始检测
const handleDetect = async () => {
  detecting.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000));
    result.value = {
      status: 'success',
      message: '检测完成',
      data: {
        // 具体结果由子组件通过插槽自定义展示
      }
    };
    ElMessage.success('检测完成');
  } catch (error) {
    ElMessage.error('检测失败');
  } finally {
    detecting.value = false;
  }
};

// 更新时间
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);
</script>

<style scoped>
.feature-page {
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
}

.feature-intro {
  margin-bottom: 32px;
}

.feature-intro h2 {
  color: #fff;
  margin: 0 0 16px 0;
}

.description {
  color: #8c8c8c;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.feature-demo {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.upload-section {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

.image-actions {
  display: flex;
  gap: 16px;
}

.upload-area {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
  background-color: #282b30;
  border: 2px dashed #404040;
}

:deep(.el-upload-dragger:hover) {
  border-color: var(--el-color-primary);
}

.upload-icon {
  font-size: 48px;
  color: #8c8c8c;
  margin-bottom: 16px;
}

.upload-text {
  color: #8c8c8c;
}

.upload-text em {
  color: var(--el-color-primary);
  font-style: normal;
}

.result-section {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.result-section h3 {
  color: #fff;
  margin: 0 0 16px 0;
}

.result-content {
  background-color: #282b30;
  border-radius: 4px;
  padding: 16px;
  color: #8c8c8c;
  font-family: monospace;
  margin: 0;
  white-space: pre-wrap;
}
</style> 
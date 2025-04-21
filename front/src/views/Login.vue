<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Message } from '@element-plus/icons-vue';

const router = useRouter();
const isLogin = ref(true);
const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
});

// 模拟用户数据
const mockUsers = [
  {
    username: 'admin',
    email: 'admin@example.com',
    password: '123456'
  }
];

// 初始化本地存储的用户数据
if (!localStorage.getItem('users')) {
  localStorage.setItem('users', JSON.stringify(mockUsers));
}

const handleSubmit = () => {
  if (isLogin.value) {
    // 登录逻辑
    if (!form.email || !form.password) {
      ElMessage.error('请填写完整信息');
      return;
    }

    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const user = users.find(u => u.email === form.email && u.password === form.password);

    if (user) {
      // 存储登录状态和用户信息
      localStorage.setItem('token', 'mock-jwt-token');
      localStorage.setItem('currentUser', JSON.stringify({
        username: user.username,
        email: user.email
      }));
      
      ElMessage.success('登录成功');
      router.push('/home');
    } else {
      ElMessage.error('邮箱或密码错误');
    }
  } else {
    // 注册逻辑
    if (!form.username || !form.email || !form.password || !form.confirmPassword) {
      ElMessage.error('请填写完整信息');
      return;
    }
    if (form.password !== form.confirmPassword) {
      ElMessage.error('两次输入的密码不一致');
      return;
    }

    const users = JSON.parse(localStorage.getItem('users') || '[]');
    
    // 检查邮箱是否已注册
    if (users.some(u => u.email === form.email)) {
      ElMessage.error('该邮箱已被注册');
      return;
    }

    // 添加新用户
    users.push({
      username: form.username,
      email: form.email,
      password: form.password
    });
    
    localStorage.setItem('users', JSON.stringify(users));
    ElMessage.success('注册成功，请登录');
    isLogin.value = true;
    
    // 清空表单
    Object.keys(form).forEach(key => form[key] = '');
  }
};

const toggleMode = () => {
  isLogin.value = !isLogin.value;
  // 清空表单
  Object.keys(form).forEach(key => form[key] = '');
};

// 添加快捷填充方法（仅用于演示）
const fillDemoAccount = () => {
  form.email = 'admin@example.com';
  form.password = '123456';
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>无人巡检系统</h2>
        <p class="subtitle">{{ isLogin ? '欢迎回来' : '创建新账户' }}</p>
      </div>

      <div class="form-container">
        <div class="form-group" v-if="!isLogin">
          <label>用户名</label>
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </div>

        <div class="form-group">
          <label>邮箱</label>
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </div>

        <div class="form-group" v-if="!isLogin">
          <label>确认密码</label>
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </div>

        <el-button 
          type="primary" 
          class="submit-btn" 
          @click="handleSubmit"
        >
          {{ isLogin ? '登 录' : '注 册' }}
        </el-button>

        <div class="form-footer">
          <span @click="toggleMode">
            {{ isLogin ? '没有账号？点击注册' : '已有账号？点击登录' }}
          </span>
          <span v-if="isLogin" class="demo-account" @click="fillDemoAccount">
            使用演示账号
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #1a1c1e;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(11, 130, 198, 0.2) 0%, transparent 40%),
    radial-gradient(circle at 75% 75%, rgba(11, 130, 198, 0.2) 0%, transparent 40%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background-color: #282b30;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #fff;
  font-size: 28px;
  margin-bottom: 8px;
  font-weight: 500;
}

.subtitle {
  color: #8c8c8c;
  font-size: 16px;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #fff;
  font-size: 14px;
  opacity: 0.9;
}

/* Element Plus 暗色主题重写 */
:deep(.el-input__wrapper) {
  background-color: #1a1c1e !important;
  box-shadow: 0 0 0 1px #303030 !important;
  border-radius: 8px;
  padding: 0 12px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #0b82c6 !important;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #0b82c6 !important;
}

:deep(.el-input__inner) {
  color: #fff !important;
  height: 40px;
}

:deep(.el-input__inner::placeholder) {
  color: #666 !important;
}

:deep(.el-input__prefix) {
  color: #666;
}

:deep(.el-input__prefix-inner) {
  display: flex;
  align-items: center;
}

:deep(.el-input__suffix) {
  color: #666;
}

:deep(.el-input__suffix-inner) {
  display: flex;
  align-items: center;
}

/* 按钮样式重写 */
:deep(.el-button) {
  border: none;
  background: linear-gradient(45deg, #0b82c6, #188df0);
  color: #fff;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(11, 130, 198, 0.3);
  background: linear-gradient(45deg, #0b82c6, #188df0);
  opacity: 0.9;
}

:deep(.el-button:active) {
  transform: translateY(0);
}

/* 密码显示切换按钮 */
:deep(.el-input__suffix-inner .el-icon) {
  color: #666;
  cursor: pointer;
}

:deep(.el-input__suffix-inner .el-icon:hover) {
  color: #0b82c6;
}

.submit-btn {
  margin-top: 20px;
  width: 100%;
}

.form-footer {
  text-align: center;
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-footer span {
  cursor: pointer;
  color: #8c8c8c;
  font-size: 14px;
  transition: color 0.3s ease;
}

.form-footer span:hover {
  color: #0b82c6;
}

.demo-account {
  font-size: 13px;
  color: #666;
  padding: 8px;
  border-radius: 4px;
  background-color: rgba(11, 130, 198, 0.1);
  transition: all 0.3s ease;
}

.demo-account:hover {
  background-color: rgba(11, 130, 198, 0.2);
  color: #0b82c6;
}

/* 添加输入框动画效果 */
:deep(.el-input__wrapper) {
  transition: all 0.3s ease;
}

:deep(.el-input__inner) {
  transition: all 0.3s ease;
}

/* 错误状态样式 */
:deep(.el-input.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #ff4d4f !important;
}

:deep(.el-input.is-error .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #ff4d4f !important;
}

/* 图标样式优化 */
:deep(.el-input__prefix-inner .el-icon),
:deep(.el-input__suffix-inner .el-icon) {
  font-size: 16px;
  transition: all 0.3s ease;
}

:deep(.el-input:hover .el-input__prefix-inner .el-icon) {
  color: #0b82c6;
}
</style>
<template>
  <div class="profile-container dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '个人信息', path: '/profile'}]" />
        
        <div class="profile-content">
          <!-- 个人信息卡片 -->
          <div class="profile-card">
            <div class="profile-header">
              <div class="avatar-section">
                <el-avatar :size="100" :src="userInfo.avatar" class="user-avatar">
                  {{ userInfo.name ? userInfo.name.charAt(0).toUpperCase() : 'U' }}
                </el-avatar>
              </div>
              <div class="basic-info">
                <h2>{{ userInfo.name || '未设置姓名' }}</h2>
                <p class="role">{{ userInfo.role }}</p>
                <p class="department">{{ userInfo.department }}</p>
              </div>
            </div>

            <!-- 信息展示与编辑表单 -->
            <el-form
              ref="formRef"
              :model="userForm"
              :rules="formRules"
              label-width="100px"
              class="profile-form"
            >
              <el-tabs v-model="activeTab" class="profile-tabs">
                <el-tab-pane label="基本信息" name="basic">
                  <el-form-item label="用户名" prop="username">
                    <el-input v-model="userForm.username" disabled />
                  </el-form-item>
                  <el-form-item label="姓名" prop="name">
                    <el-input v-model="userForm.name" :disabled="!isEditing" />
                  </el-form-item>
                  <el-form-item label="手机号码" prop="phone">
                    <el-input v-model="userForm.phone" :disabled="!isEditing" />
                  </el-form-item>
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="userForm.email" :disabled="!isEditing" />
                  </el-form-item>
                  <el-form-item label="所属部门">
                    <el-input v-model="userForm.department" disabled />
                  </el-form-item>
                  <el-form-item label="角色">
                    <el-input v-model="userForm.role" disabled />
                  </el-form-item>
                </el-tab-pane>

                <el-tab-pane label="安全设置" name="security">
                  <el-form-item label="原密码" prop="oldPassword" v-if="isChangingPassword">
                    <el-input
                      v-model="userForm.oldPassword"
                      type="password"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword" v-if="isChangingPassword">
                    <el-input
                      v-model="userForm.newPassword"
                      type="password"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword" v-if="isChangingPassword">
                    <el-input
                      v-model="userForm.confirmPassword"
                      type="password"
                      show-password
                    />
                  </el-form-item>
                  
                  <div class="security-items">
                    <div class="security-item">
                      <div class="security-info">
                        <h4>账户密码</h4>
                        <p>定期更改密码可以保护账号安全</p>
                      </div>
                      <el-button 
                        type="primary" 
                        link
                        @click="togglePasswordChange"
                      >
                        {{ isChangingPassword ? '取消修改' : '修改密码' }}
                      </el-button>
                    </div>
                  </div>
                </el-tab-pane>

                <el-tab-pane label="消息通知" name="notification">
                  <div class="notification-settings">
                    <div class="notification-item">
                      <div class="notification-info">
                        <h4>系统消息通知</h4>
                        <p>接收系统更新、维护等通知</p>
                      </div>
                      <el-switch v-model="notificationSettings.system" />
                    </div>
                    
                    <div class="notification-item">
                      <div class="notification-info">
                        <h4>报警消息通知</h4>
                        <p>接收设备报警、异常等通知</p>
                      </div>
                      <el-switch v-model="notificationSettings.alarm" />
                    </div>
                    
                    <div class="notification-item">
                      <div class="notification-info">
                        <h4>声音提醒</h4>
                        <p>新消息时播放提示音</p>
                      </div>
                      <el-switch 
                        v-model="notificationSettings.sound"
                        :disabled="!notificationSettings.system && !notificationSettings.alarm"
                      />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>

              <div class="form-actions" v-if="activeTab === 'basic'">
                <el-button @click="toggleEdit" v-if="!isEditing">编辑信息</el-button>
                <template v-else>
                  <el-button @click="cancelEdit">取消</el-button>
                  <el-button type="primary" @click="saveChanges">保存</el-button>
                </template>
              </div>

              <div class="form-actions" v-if="activeTab === 'security' && isChangingPassword">
                <el-button @click="togglePasswordChange">取消</el-button>
                <el-button type="primary" @click="savePassword">保存</el-button>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const activeTab = ref('basic');
const isEditing = ref(false);
const isChangingPassword = ref(false);
const formRef = ref(null);

// 用户信息
const userInfo = ref({
  username: '',
  name: '',
  role: '',
  department: '',
  phone: '',
  avatar: ''
});

// 表单数据
const userForm = reactive({
  username: userInfo.value.username,
  name: userInfo.value.name,
  phone: userInfo.value.phone,
  department: userInfo.value.department,
  role: userInfo.value.role,
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 通知设置
const notificationSettings = reactive({
  system: true,
  alarm: true,
  sound: true
});

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.newPassword) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

// 更新用户信息
const updateUserInfo = () => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
  userInfo.value = {
    username: currentUser.username || '',
    name: currentUser.name || '',
    // 根据roleType转换角色显示文本
    role: currentUser.roleType === 1 ? '管理员' : '普通用户',
    department: currentUser.department || '未分配',
    phone: currentUser.phoneNumber || '',
    // 不使用实际头像，而是使用姓名首字母
    avatar: ''
  };
  
  // 更新表单数据
  Object.assign(userForm, {
    username: userInfo.value.username,
    name: userInfo.value.name,
    phone: userInfo.value.phone,
    department: userInfo.value.department,
    role: userInfo.value.role
  });
};

// 切换编辑状态
const toggleEdit = () => {
  isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  Object.assign(userForm, {
    name: userInfo.value.name,
    phone: userInfo.value.phone
  });
};

// 保存修改
const saveChanges = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用后端 API
        Object.assign(userInfo.value, {
          name: userForm.name,
          phone: userForm.phone
        });
        ElMessage.success('保存成功');
        isEditing.value = false;
      } catch (error) {
        ElMessage.error('保存失败');
      }
    }
  });
};

// 切换密码修改状态
const togglePasswordChange = () => {
  isChangingPassword.value = !isChangingPassword.value;
  if (!isChangingPassword.value) {
    userForm.oldPassword = '';
    userForm.newPassword = '';
    userForm.confirmPassword = '';
  }
};

// 保存密码修改
const savePassword = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用后端 API
        ElMessage.success('密码修改成功');
        togglePasswordChange();
      } catch (error) {
        ElMessage.error('密码修改失败');
      }
    }
  });
};

// 在组件挂载时更新用户信息
onMounted(() => {
  updateUserInfo();
});
</script>

<style scoped>
.profile-container {
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

.profile-content {
  margin-top: 20px;
}

.profile-card {
  background-color: #282b30;
  border-radius: 8px;
  padding: 24px;
}

.profile-header {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.avatar-section {
  text-align: center;
}

.user-avatar {
  background-color: #1890ff;
  color: #fff;
  font-size: 36px;
  font-weight: bold;
}

.basic-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.basic-info h2 {
  margin: 0 0 8px 0;
  color: #fff;
  font-size: 24px;
}

.role {
  color: #8c8c8c;
  margin: 0 0 4px 0;
}

.department {
  color: #8c8c8c;
  margin: 0;
}

.profile-form {
  max-width: 600px;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.security-items,
.notification-settings {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.security-item,
.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #1a1c1e;
  border-radius: 4px;
}

.security-info,
.notification-info {
  flex: 1;
}

.security-info h4,
.notification-info h4 {
  margin: 0 0 4px 0;
  color: #fff;
  font-size: 16px;
}

.security-info p,
.notification-info p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

:deep(.el-tabs__item) {
  color: #8c8c8c !important;
}

:deep(.el-tabs__item.is-active) {
  color: #fff !important;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #303030 !important;
}

:deep(.el-input__inner) {
  background-color: #1a1c1e !important;
  border-color: #303030 !important;
  color: #fff !important;
}

:deep(.el-input__inner:disabled) {
  background-color: #282b30 !important;
  color: #8c8c8c !important;
}

:deep(.el-form-item__label) {
  color: #8c8c8c !important;
}

:deep(.el-dialog) {
  background-color: #282b30 !important;
}

:deep(.el-dialog__title) {
  color: #fff !important;
}

:deep(.el-dialog__body) {
  color: #fff !important;
}

:deep(.el-select) {
  width: 100%;
}
</style> 
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
                <el-avatar :size="100" :src="userInfo.avatar">
                  {{ userInfo.username?.charAt(0).toUpperCase() }}
                </el-avatar>
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-button type="primary" link>更换头像</el-button>
                </el-upload>
              </div>
              <div class="basic-info">
                <h2>{{ userInfo.name || userInfo.username }}</h2>
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
                    
                    <div class="security-item">
                      <div class="security-info">
                        <h4>手机绑定</h4>
                        <p>已绑定：{{ maskPhone(userForm.phone) }}</p>
                      </div>
                      <el-button type="primary" link @click="handlePhoneChange">
                        更换手机号
                      </el-button>
                    </div>
                    
                    <div class="security-item">
                      <div class="security-info">
                        <h4>邮箱绑定</h4>
                        <p>已绑定：{{ maskEmail(userForm.email) }}</p>
                      </div>
                      <el-button type="primary" link @click="handleEmailChange">
                        更换邮箱
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
                    
                    <div class="notification-item">
                      <div class="notification-info">
                        <h4>邮件通知</h4>
                        <p>重要消息将同步发送邮件提醒</p>
                      </div>
                      <el-switch v-model="notificationSettings.email" />
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

    <!-- 手机号修改对话框 -->
    <el-dialog
      v-model="phoneDialogVisible"
      title="更换手机号"
      width="400px"
    >
      <el-form
        ref="phoneFormRef"
        :model="phoneForm"
        :rules="phoneRules"
        label-width="100px"
      >
        <el-form-item label="当前手机号">
          <el-input v-model="userForm.phone" disabled />
        </el-form-item>
        <el-form-item label="新手机号" prop="newPhone">
          <el-input v-model="phoneForm.newPhone" />
        </el-form-item>
        <el-form-item label="验证码" prop="verifyCode">
          <div class="verify-code-input">
            <el-input v-model="phoneForm.verifyCode" />
            <el-button 
              type="primary" 
              :disabled="!!countdown"
              @click="sendPhoneCode"
            >
              {{ countdown ? `${countdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="phoneDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePhoneChange">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 邮箱修改对话框 -->
    <el-dialog
      v-model="emailDialogVisible"
      title="更换邮箱"
      width="400px"
    >
      <el-form
        ref="emailFormRef"
        :model="emailForm"
        :rules="emailRules"
        label-width="100px"
      >
        <el-form-item label="当前邮箱">
          <el-input v-model="userForm.email" disabled />
        </el-form-item>
        <el-form-item label="新邮箱" prop="newEmail">
          <el-input v-model="emailForm.newEmail" />
        </el-form-item>
        <el-form-item label="验证码" prop="verifyCode">
          <div class="verify-code-input">
            <el-input v-model="emailForm.verifyCode" />
            <el-button 
              type="primary" 
              :disabled="!!emailCountdown"
              @click="sendEmailCode"
            >
              {{ emailCountdown ? `${emailCountdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="emailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEmailChange">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
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
const phoneFormRef = ref(null);
const emailFormRef = ref(null);

// 用户信息
const userInfo = ref({
  username: 'admin',
  name: '管理员',
  role: '系统管理员',
  department: '系统管理部',
  phone: '13800138000',
  email: 'admin@example.com',
  avatar: ''
});

// 表单数据
const userForm = reactive({
  username: userInfo.value.username,
  name: userInfo.value.name,
  phone: userInfo.value.phone,
  email: userInfo.value.email,
  department: userInfo.value.department,
  role: userInfo.value.role,
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 手机号修改表单
const phoneDialogVisible = ref(false);
const phoneForm = reactive({
  newPhone: '',
  verifyCode: ''
});
const countdown = ref(0);

// 邮箱修改表单
const emailDialogVisible = ref(false);
const emailForm = reactive({
  newEmail: '',
  verifyCode: ''
});
const emailCountdown = ref(0);

// 通知设置
const notificationSettings = reactive({
  system: true,
  alarm: true,
  sound: true,
  email: false
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
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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

// 手机号验证规则
const phoneRules = {
  newPhone: [
    { required: true, message: '请输入新手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
  ]
};

// 邮箱验证规则
const emailRules = {
  newEmail: [
    { required: true, message: '请输入新邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
  ]
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
    phone: userInfo.value.phone,
    email: userInfo.value.email
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
          phone: userForm.phone,
          email: userForm.email
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

// 处理手机号修改
const handlePhoneChange = () => {
  phoneForm.newPhone = '';
  phoneForm.verifyCode = '';
  phoneDialogVisible.value = true;
};

// 发送手机验证码
const sendPhoneCode = async () => {
  if (!phoneForm.newPhone) {
    ElMessage.warning('请输入新手机号');
    return;
  }
  
  try {
    // 这里应该调用后端 API 发送验证码
    countdown.value = 60;
    const timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
    ElMessage.success('验证码已发送');
  } catch (error) {
    ElMessage.error('验证码发送失败');
  }
};

// 保存手机号修改
const savePhoneChange = async () => {
  if (!phoneFormRef.value) return;
  
  await phoneFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用后端 API
        userForm.phone = phoneForm.newPhone;
        userInfo.value.phone = phoneForm.newPhone;
        ElMessage.success('手机号修改成功');
        phoneDialogVisible.value = false;
      } catch (error) {
        ElMessage.error('手机号修改失败');
      }
    }
  });
};

// 处理邮箱修改
const handleEmailChange = () => {
  emailForm.newEmail = '';
  emailForm.verifyCode = '';
  emailDialogVisible.value = true;
};

// 发送邮箱验证码
const sendEmailCode = async () => {
  if (!emailForm.newEmail) {
    ElMessage.warning('请输入新邮箱');
    return;
  }
  
  try {
    // 这里应该调用后端 API 发送验证码
    emailCountdown.value = 60;
    const timer = setInterval(() => {
      emailCountdown.value--;
      if (emailCountdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
    ElMessage.success('验证码已发送');
  } catch (error) {
    ElMessage.error('验证码发送失败');
  }
};

// 保存邮箱修改
const saveEmailChange = async () => {
  if (!emailFormRef.value) return;
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里应该调用后端 API
        userForm.email = emailForm.newEmail;
        userInfo.value.email = emailForm.newEmail;
        ElMessage.success('邮箱修改成功');
        emailDialogVisible.value = false;
      } catch (error) {
        ElMessage.error('邮箱修改失败');
      }
    }
  });
};

// 头像上传前的处理
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }

  // 这里应该调用后端 API 上传头像
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => {
    userInfo.value.avatar = reader.result;
    ElMessage.success('头像上传成功');
  };
  return false;
};

// 掩码处理手机号
const maskPhone = (phone) => {
  return phone ? phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '';
};

// 掩码处理邮箱
const maskEmail = (email) => {
  if (!email) return '';
  const [name, domain] = email.split('@');
  return `${name.charAt(0)}****@${domain}`;
};
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

.avatar-uploader {
  margin-top: 12px;
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

.verify-code-input {
  display: flex;
  gap: 12px;
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
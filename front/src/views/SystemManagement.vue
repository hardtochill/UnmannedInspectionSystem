<template>
  <div class="system-management dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '系统管理', path: '/system-management'}]" />
        
        <div class="content-section">
          <div class="section-header">
            <h3>用户管理</h3>
            <div class="operation-buttons">
              <el-button type="primary" @click="addUser">
                <el-icon><Plus /></el-icon>新增用户
              </el-button>
              <el-button @click="batchDelete" :disabled="!selectedUsers.length">
                <el-icon><Delete /></el-icon>批量删除
              </el-button>
            </div>
          </div>

          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入用户名/姓名搜索"
              class="filter-item"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="roleFilter" placeholder="角色" class="filter-item" clearable>
              <el-option label="管理员" value="admin" />
              <el-option label="操作员" value="operator" />
              <el-option label="普通用户" value="user" />
            </el-select>
            <el-select v-model="statusFilter" placeholder="状态" class="filter-item" clearable>
              <el-option label="启用" value="active" />
              <el-option label="禁用" value="inactive" />
            </el-select>
            <el-button type="primary" @click="query">查询</el-button>
            <el-button @click="reset">重置</el-button>
          </div>

          <el-table 
            :data="userData" 
            style="width: 100%"
            class="custom-table"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="role" label="角色">
              <template #default="{ row }">
                <el-tag :type="getRoleType(row.role)">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="department" label="所属部门" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '启用' ? 'success' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastLogin" label="最后登录时间" />
            <el-table-column label="操作" width="250">
              <template #default="{ row }">
                <el-button type="primary" link @click="editUser(row)">编辑</el-button>
                <el-button type="primary" link @click="changeRole(row)">权限</el-button>
                <el-button 
                  type="primary" 
                  link 
                  @click="toggleUserStatus(row)"
                >
                  {{ row.status === '启用' ? '禁用' : '启用' }}
                </el-button>
                <el-button type="danger" link @click="deleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="dialogType === 'add'">
          <el-input v-model="userForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="所属部门" prop="department">
          <el-select v-model="userForm.department" placeholder="请选择部门">
            <el-option
              v-for="dept in departments"
              :key="dept.value"
              :label="dept.label"
              :value="dept.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="权限设置"
      width="500px"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        label-width="100px"
      >
        <el-form-item label="用户角色" prop="role">
          <el-select v-model="roleForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="管理员" />
            <el-option label="操作员" value="操作员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限范围">
          <el-tree
            ref="permissionTree"
            :data="permissionList"
            show-checkbox
            node-key="id"
            :default-checked-keys="roleForm.permissions"
            :props="{ label: 'name' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoleForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Plus, Delete, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

const searchKeyword = ref('');
const roleFilter = ref('');
const statusFilter = ref('');
const selectedUsers = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(100);

const userData = ref([
  { 
    username: 'admin',
    name: '管理员',
    role: '超级管理员',
    department: '系统管理部',
    status: '启用',
    lastLogin: '2024-04-19 10:30:00'
  },
  { 
    username: 'operator1',
    name: '张三',
    role: '操作员',
    department: '运维部',
    status: '启用',
    lastLogin: '2024-04-19 09:15:00'
  },
  { 
    username: 'user1',
    name: '李四',
    role: '普通用户',
    department: '生产部',
    status: '禁用',
    lastLogin: '2024-04-18 17:30:00'
  }
]);

const getRoleType = (role) => {
  const types = {
    '超级管理员': 'danger',
    '管理员': 'warning',
    '操作员': 'success',
    '普通用户': 'info'
  };
  return types[role] || 'info';
};

const userDialogVisible = ref(false);
const roleDialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const userFormRef = ref(null);
const roleFormRef = ref(null);
const permissionTree = ref(null);

const userForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  department: ''
});

const roleForm = reactive({
  userId: '',
  role: '',
  permissions: []
});

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ]
};

const departments = [
  { label: '系统管理部', value: 'system' },
  { label: '运维部', value: 'operation' },
  { label: '生产部', value: 'production' }
];

const permissionList = [
  {
    id: 1,
    name: '系统管理',
    children: [
      { id: 11, name: '用户管理' },
      { id: 12, name: '角色管理' },
      { id: 13, name: '权限设置' }
    ]
  },
  {
    id: 2,
    name: '设备管理',
    children: [
      { id: 21, name: '设备监控' },
      { id: 22, name: '报警管理' },
      { id: 23, name: '维护记录' }
    ]
  }
];

const query = async () => {
  const filteredData = userData.value.filter(user => {
    const matchKeyword = !searchKeyword.value || 
      user.username.includes(searchKeyword.value) || 
      user.name.includes(searchKeyword.value);
    const matchRole = !roleFilter.value || user.role === roleFilter.value;
    const matchStatus = !statusFilter.value || user.status === statusFilter.value;
    return matchKeyword && matchRole && matchStatus;
  });
  
  total.value = filteredData.length;
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  userData.value = filteredData.slice(start, end);
};

const resetUserForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields();
  }
  Object.assign(userForm, {
    username: '',
    name: '',
    password: '',
    confirmPassword: '',
    department: ''
  });
};

const addUser = () => {
  dialogType.value = 'add';
  resetUserForm();
  userDialogVisible.value = true;
};

const editUser = (row) => {
  dialogType.value = 'edit';
  resetUserForm();
  Object.assign(userForm, {
    username: row.username,
    name: row.name,
    department: row.department
  });
  userDialogVisible.value = true;
};

const submitUserForm = async () => {
  if (!userFormRef.value) return;
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          userData.value.push({
            ...userForm,
            role: '普通用户',
            status: '启用',
            lastLogin: '-'
          });
          ElMessage.success('添加用户成功');
        } else {
          const index = userData.value.findIndex(u => u.username === userForm.username);
          if (index !== -1) {
            userData.value[index] = {
              ...userData.value[index],
              name: userForm.name,
              department: userForm.department
            };
          }
          ElMessage.success('更新用户成功');
        }
        userDialogVisible.value = false;
        query();
      } catch (error) {
        ElMessage.error('操作失败');
      }
    }
  });
};

const changeRole = (row) => {
  roleForm.userId = row.username;
  roleForm.role = row.role;
  roleForm.permissions = [11, 21];
  roleDialogVisible.value = true;
};

const submitRoleForm = async () => {
  try {
    const checkedNodes = permissionTree.value.getCheckedNodes();
    const permissions = checkedNodes.map(node => node.id);
    
    const index = userData.value.findIndex(u => u.username === roleForm.userId);
    if (index !== -1) {
      userData.value[index].role = roleForm.role;
    }
    
    ElMessage.success('权限设置成功');
    roleDialogVisible.value = false;
    query();
  } catch (error) {
    ElMessage.error('设置失败');
  }
};

const toggleUserStatus = async (row) => {
  try {
    const newStatus = row.status === '启用' ? '禁用' : '启用';
    const index = userData.value.findIndex(u => u.username === row.username);
    if (index !== -1) {
      userData.value[index].status = newStatus;
    }
    ElMessage.success(`${newStatus}成功`);
  } catch (error) {
    ElMessage.error('操作失败');
  }
};

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该用户吗？此操作不可恢复',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    userData.value = userData.value.filter(u => u.username !== row.username);
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDelete = async () => {
  if (!selectedUsers.value.length) {
    ElMessage.warning('请选择要删除的用户');
    return;
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    const usernames = selectedUsers.value.map(u => u.username);
    userData.value = userData.value.filter(u => !usernames.includes(u.username));
    ElMessage.success('批量删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const handleSelectionChange = (val) => {
  selectedUsers.value = val;
};

const reset = () => {
  searchKeyword.value = '';
  roleFilter.value = '';
  statusFilter.value = '';
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  query();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  query();
};

onMounted(() => {
  query();
});
</script>

<style scoped>
.system-management {
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
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  color: #fff;
  margin: 0;
}

.operation-buttons {
  display: flex;
  gap: 12px;
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
}

.filter-item {
  width: 200px;
}

.custom-table {
  margin-bottom: 20px;
}

:deep(.el-table) {
  background-color: transparent !important;
}

:deep(.el-table th) {
  background-color: #1a1c1e !important;
  border-bottom: 1px solid #303030;
  color: #8c8c8c;
}

:deep(.el-table td) {
  background-color: transparent !important;
  border-bottom: 1px solid #303030;
  color: #fff;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #1a1c1e !important;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #8c8c8c;
  --el-pagination-button-color: #fff;
  --el-pagination-button-bg-color: #1a1c1e;
  --el-pagination-button-disabled-color: #606266;
  --el-pagination-button-disabled-bg-color: #282b30;
  --el-pagination-hover-color: var(--el-color-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-dialog) {
  background-color: #282b30;
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-dialog__body) {
  color: #fff;
}

:deep(.el-form-item__label) {
  color: #8c8c8c !important;
}

:deep(.el-input__inner),
:deep(.el-select .el-input__inner) {
  background-color: #1a1c1e !important;
  border-color: #303030 !important;
  color: #fff !important;
}

:deep(.el-tree) {
  background-color: #1a1c1e !important;
  color: #fff !important;
}

:deep(.el-tree-node__content:hover) {
  background-color: #282b30 !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #282b30 !important;
}
</style>
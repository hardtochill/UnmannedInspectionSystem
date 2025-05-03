import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { UserInfo } from '@/types/user';

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<UserInfo | null>(null);

  // 设置用户信息
  const setUser = (user: UserInfo) => {
    currentUser.value = user;
  };

  // 清除用户信息
  const clearUser = () => {
    currentUser.value = null;
  };

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!currentUser.value?.token;
  };

  // 获取用户token
  const getToken = () => {
    return currentUser.value?.token;
  };

  // 检查是否是管理员
  const isAdmin = () => {
    return currentUser.value?.roleType === 1;
  };

  return {
    currentUser,
    setUser,
    clearUser,
    isLoggedIn,
    getToken,
    isAdmin
  };
}, {
  persist: true // 启用状态持久化
}); 
/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-19 23:18:07
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-30 19:49:02
 * @FilePath: \front\src\api\index.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import axios from 'axios'
import type { DeviceStatus, AlarmRecord, ShutdownRecord, SystemLog, UserInfo, ApiResponse } from './types'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',  // 统一使用 /api 前缀
  timeout: 5000
})

// 封装请求方法
const http = {
  async post(url: string, data?: any) {
    try {
      console.log('准备发送请求:', { url, data });
      const token = JSON.parse(localStorage.getItem('currentUser') || '{}').token;
      
      const config: any = {
        method: 'POST',
        url,
        data: new URLSearchParams(data)
      };

      // 除了登录请求外，其他请求都需要带上 token
      if (token ) {
        config.headers = {
          'token': token
        };
      }

      const response = await request(config);
      console.log('请求响应:', response);
      return response.data;
    } catch (error) {
      console.error('请求错误:', error);
      throw error;
    }
  },

  handleError(error: any) {
    if (error.response?.status === 401) {
      console.warn('Token无效或过期，退出登录');
      localStorage.clear();
      window.location.href = '/login';
    }
  }
};

// 停机记录相关接口
export const shutdownApi = {
  async getShutdownList(): Promise<ShutdownRecord[]> {
    const response = await http.post('/shutdown/list')
    return response.data.data
  },
  
  async addShutdownRecord(record: Omit<ShutdownRecord, 'id'>): Promise<void> {
    await http.post('/shutdown/add', record)
  }
}

// 系统管理相关接口
export const systemApi = {
  async getSystemLogs(): Promise<SystemLog[]> {
    const response = await http.post('/system/logs')
    return response.data.data
  },
  
  async getUserList(): Promise<UserInfo[]> {
    const response = await http.post('/user/list')
    return response.data.data
  },
  
  async updateUserStatus(id: string, status: UserInfo['status']): Promise<void> {
    await http.post('/user/' + id + '/status', { status })
  }
}

// 账号相关接口
export const accountApi = {
  async login(formData: { phoneNumber: string; password: string }) {
    return http.post('/account/login', formData);
  },

  async logout(userId: number) {
    return http.post('/account/logout', { userId });
  },
  
  async register(data: { username: string; phoneNumber: string; password: string }): Promise<void> {
    await http.post('/account/register', data);
  }
};

// 其他 API 方法也都使用 POST
export const deviceApi = {
  async getList() {
    return http.post('/device/list');
  },
  // ... 其他方法
};

// ... 其他 API 对象 
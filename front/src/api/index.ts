/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-19 23:18:07
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-03 17:16:54
 * @FilePath: \front\src\api\index.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import axios from 'axios'
// import type { DeviceStatus, AlarmRecord, ShutdownRecord, SystemLog, UserInfo, ApiResponse } from './types'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',  // 统一使用 /api 前缀
  timeout: 5000
})

// 封装请求方法
const http = {
  async post(url: string, data?: any) {
    try {
      // console.log('准备发送请求:', { url, data });
      const userStore = useUserStore();
      const token = userStore.getToken();
      
      // 将所有数据转换为 FormData 格式
      const formData = new URLSearchParams();
      if (data) {
        Object.entries(data).forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            formData.append(key, value.toString());
          }
        });
      }

      const config: any = {
        method: 'POST',
        url,
        data: formData
      };

      // 除了登录请求外，其他请求都需要带上 token
      if (token) {
        config.headers = {
          'token': token
        };
      }

      const response = await request(config);
      // console.log('请求响应:', response);
      return response.data;
    } catch (error) {
      // console.error('请求错误:', error);
      throw error;
    }
  },

  handleError(error: any) {
    if (error.response?.status === 401) {
      // console.warn('Token无效或过期，退出登录');
      const userStore = useUserStore();
      userStore.clearUser();
      window.location.href = '/login';
    }
  }
};

// 工作台相关接口
export const workspaceApi = {
  // 获取工作台概览数据
  async getOverview() {
    return http.post('/workspace/overview');
  },
  
  // 获取设备状态统计
  async getDeviceStats() {
    return http.post('/workspace/deviceStats');
  },
  
  // 获取报警趋势数据
  async getAlarmTrend() {
    return http.post('/workspace/alarmTrend');
  },
  
  // 获取待处理报警列表
  async getPendingAlarms(params: {
    pageNo: number;
    pageSize: number;
    status?: string;
  }) {
    return http.post('/workspace/pendingAlarms', params);
  }
};

// 系统管理相关接口
export const systemApi = {

  // 获取用户列表
  async getUserList(params: {
    pageNo: number;
    pageSize: number;
    name?: string;
    roleType?: number;
    status?: number;
  }) {
    return http.post('/user/loadList', params);
  },
  
};

// 账号相关接口
export const accountApi = {
  async login(formData: { phoneNumber: string; password: string }) {
    return http.post('/account/login', formData);
  },

  async logout(userId: number) {
    return http.post('/account/logout', { userId });
  },
  
  async register(data: { username: string; phoneNumber: string; password: string }) {
    return http.post('/account/register', data);
  },
  async changePassword(data: { userId:number; oldPassword:string; newPassword:string }){
    return http.post('/account/changePassword', data);
  }
};

// 设备相关接口
export const deviceApi = {
  async getList() {
    return http.post('/device/list');
  },
  
  // 获取设备状态统计
  async countStatus(): Promise<ApiResponseType<{
    runningCount: number;    // 运行中设备数量
    alarmCount: number;      // 报警设备数量
    runningPercent: number;  // 运行中设备百分比
    alarmPercent: number;    // 报警设备百分比
  }>> {
    return http.post('/device/countStatus');
  },

  async countType(){
    return http.post('/device/countType');
  }
};

// 报警管理相关接口
export const alarmApi = {
  // 获取报警列表
  async loadList(params: {
    pageNo: number;
    pageSize: number;
    deviceType?: number;
    status?: number;
    startTime?: string;
    endTime?: string;
  }) {
    return http.post('/alarm/loadList', params);
  },
  
  // 处理报警
  async handleAlarm(alarmId: number): Promise<ApiResponseType<any>> {
    return http.post('/alarm/changeToProcessed', { alarmId });
  },
  
  // 获取报警状态统计
  async getStatusCount(): Promise<ApiResponseType<{
    processedCount: number;    // 已处理数量
    unprocessedCount: number;  // 未处理数量
  }>> {
    return http.post('/alarm/countStatus');
  },

  async countType(){
    return http.post('/alarm/countType');
  },

  async countAlarmTimeByMonth(){
    return http.post('/alarm/countAlarmTimeByMonth');
  },

  async countProcessedTimeByMonth(){
    return http.post('/alarm/countProcessedTimeByMonth');
  }
  
};

// 添加用户相关的类型定义
interface UserListItem {
  userId: number;  // 添加 userId 字段
  name: string;
  phoneNumber: string;
  roleType: number;
  status: number;
}

interface UserListResponse {
  list: UserListItem[];
  pageNo: number;
  pageSize: number;
  pageTotal: number;
}

// 重命名接口以避免冲突
interface ApiResponseType<T> {
  code: number;
  data: T;
  info: string;
}

//用户管理
export const userApi = {
  async update(data: {
    userId?: number;
    name: string;
    phoneNumber: string;
    roleType: number;
  }): Promise<ApiResponseType<any>> {
    // 根据是否有userId来判断是创建还是更新
    const url = '/user/createOrUpdate';
    return http.post(url, data);
  },

  async delete(userId: number): Promise<ApiResponseType<any>> {
    return http.post('/user/delete', { userId });
  },

  async loadList(params: {
    pageNo: number;
    pageSize: number;
    name?: string;
    roleType?: number;
    status?: number;
  }): Promise<ApiResponseType<UserListResponse>> {
    return http.post('/user/loadList', params);
  }
};

// 测点相关接口
interface MeasurementPointItem {
  id: number;
  deviceName: string;
  measuringPointName: string;
  measuringPointStatus: number;
  workshop: string;
  description?: string;
}

interface MeasurementPointResponse {
  list: MeasurementPointItem[];
  pageNo: number;
  pageSize: number;
  pageTotal: number;
}

export const mpApi = {
  // 获取测点列表
  async loadList(params: {
    pageNo: number;
    pageSize: number;
    workshop?: string;
    deviceName?: string;
    measuringPointStatus?: number;
    measuringPointName?: string;
    workshopName?:string;
  }): Promise<ApiResponseType<MeasurementPointResponse>> {
    return http.post('/mp/loadList', params);
  },
  async loadImage(params: {
    mpId: number;
  }): Promise<ApiResponseType<any>> {
    // console.log('loadImage API params:', params);
    return http.post('/mp/loadImage', params);
  },
  async getDetail(params: {
    mpId: number;
  }): Promise<ApiResponseType<any>> {
    return http.post('/mp/getDetail', params);
  }
};
export const workshopApi={
  async countDevice(){
    return http.post('/workshop/countDevice');
  }
}

export const detectApi = {
  async detect(fileData: FormData, modelName: string): Promise<ApiResponseType<any>> {
    try {
      // 添加 modelName 字段到 FormData 中
      fileData.append('modelName', modelName);

      const userStore = useUserStore();
      const token = userStore.getToken();

      // 使用 axios 直接发送请求
      const response = await axios({
        method: 'POST',
        url: '/api/detect/detect',
        data: fileData,
        headers: {
          'Content-Type': 'multipart/form-data',
          'token': token
        }
      });
      return response.data;
    } catch (error) {
      // console.error('检测请求错误:', error);
      throw error;
    }
  }
}

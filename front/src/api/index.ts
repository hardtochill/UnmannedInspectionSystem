import { ref } from 'vue'
import type { DeviceStatus, AlarmRecord, ShutdownRecord, SystemLog, UserInfo, ApiResponse } from './types'

// 模拟数据
const mockDeviceStatus: DeviceStatus[] = [
  { id: '1', name: '设备A', status: 'normal', location: '北京', lastUpdate: '2024-03-20 10:00:00' },
  { id: '2', name: '设备B', status: 'warning', location: '上海', lastUpdate: '2024-03-20 09:30:00' },
  { id: '3', name: '设备C', status: 'error', location: '广州', lastUpdate: '2024-03-20 09:00:00' },
  { id: '4', name: '设备D', status: 'offline', location: '深圳', lastUpdate: '2024-03-20 08:30:00' }
]

const mockAlarmRecords: AlarmRecord[] = [
  { id: '1', deviceId: '1', deviceName: '设备A', level: 'high', type: 'temperature', message: '温度过高', timestamp: '2024-03-20 10:00:00', status: 'unread' },
  { id: '2', deviceId: '2', deviceName: '设备B', level: 'medium', type: 'pressure', message: '压力异常', timestamp: '2024-03-20 09:30:00', status: 'read' },
  { id: '3', deviceId: '3', deviceName: '设备C', level: 'low', type: 'vibration', message: '振动异常', timestamp: '2024-03-20 09:00:00', status: 'handled' }
]

const mockShutdownRecords: ShutdownRecord[] = [
  { id: '1', deviceId: '1', deviceName: '设备A', startTime: '2024-03-20 10:00:00', endTime: '2024-03-20 11:00:00', duration: 60, reason: '例行维护', operator: '张三' },
  { id: '2', deviceId: '2', deviceName: '设备B', startTime: '2024-03-20 09:30:00', endTime: '2024-03-20 10:30:00', duration: 60, reason: '故障维修', operator: '李四' }
]

const mockSystemLogs: SystemLog[] = [
  { id: '1', type: 'info', message: '系统启动', timestamp: '2024-03-20 10:00:00', operator: '系统' },
  { id: '2', type: 'warning', message: '内存使用率过高', timestamp: '2024-03-20 09:30:00', operator: '系统' },
  { id: '3', type: 'error', message: '数据库连接失败', timestamp: '2024-03-20 09:00:00', operator: '系统' }
]

const mockUsers: UserInfo[] = [
  { id: '1', username: 'admin', role: 'admin', lastLogin: '2024-03-20 10:00:00', status: 'active' },
  { id: '2', username: 'operator', role: 'operator', lastLogin: '2024-03-20 09:30:00', status: 'active' },
  { id: '3', username: 'viewer', role: 'viewer', lastLogin: '2024-03-20 09:00:00', status: 'inactive' }
]

// 模拟 API 请求延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// 设备状态相关接口
export const deviceApi = {
  async getDeviceStatusList(): Promise<DeviceStatus[]> {
    await delay(500);
    return [
      {
        id: '1',
        name: '设备1',
        status: 'normal',
        location: '位置1',
        lastUpdate: new Date().toISOString()
      },
      {
        id: '2',
        name: '设备2',
        status: 'warning',
        location: '位置2',
        lastUpdate: new Date().toISOString()
      }
    ];
  },
  
  async updateDeviceStatus(id: string, status: DeviceStatus['status']): Promise<ApiResponse<null>> {
    await delay(500);
    return {
      code: 200,
      message: '更新成功',
      data: null
    };
  }
}

// 报警管理相关接口
export const alarmApi = {
  async getAlarmList(): Promise<AlarmRecord[]> {
    await delay(500);
    return [
      {
        id: '1',
        deviceId: '1',
        deviceName: '设备1',
        level: 'warning',
        message: '设备温度过高',
        time: new Date().toISOString(),
        status: 'pending'
      },
      {
        id: '2',
        deviceId: '2',
        deviceName: '设备2',
        level: 'error',
        message: '设备离线',
        time: new Date().toISOString(),
        status: 'processing'
      }
    ];
  },
  
  async updateAlarmStatus(id: string, status: AlarmRecord['status']): Promise<ApiResponse<null>> {
    await delay(500);
    return {
      code: 200,
      message: '更新成功',
      data: null
    };
  }
}

// 停机记录相关接口
export const shutdownApi = {
  getShutdownList: () => {
    return new Promise<ShutdownRecord[]>((resolve) => {
      setTimeout(() => {
        resolve(mockShutdownRecords)
      }, 500)
    })
  },
  
  addShutdownRecord: (record: Omit<ShutdownRecord, 'id'>) => {
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        const newRecord = {
          ...record,
          id: String(mockShutdownRecords.length + 1)
        }
        mockShutdownRecords.push(newRecord)
        resolve()
      }, 500)
    })
  }
}

// 系统管理相关接口
export const systemApi = {
  getSystemLogs: () => {
    return new Promise<SystemLog[]>((resolve) => {
      setTimeout(() => {
        resolve(mockSystemLogs)
      }, 500)
    })
  },
  
  getUserList: () => {
    return new Promise<UserInfo[]>((resolve) => {
      setTimeout(() => {
        resolve(mockUsers)
      }, 500)
    })
  },
  
  updateUserStatus: (id: string, status: UserInfo['status']) => {
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        const user = mockUsers.find(u => u.id === id)
        if (user) {
          user.status = status
        }
        resolve()
      }, 500)
    })
  }
} 
// 设备状态类型
export type DeviceStatus = {
  id: string;
  name: string;
  status: 'alarm' | 'warning' | 'normal' | 'offline' | 'shutdown';
  location: string;
  lastUpdate: string;
  type: string;
  description: string;
  imageUrl?: string;
  tags?: string[];
};

// 报警记录类型
export type AlarmRecord = {
  id: string;
  deviceId: string;
  deviceName: string;
  level: 'error' | 'warning' | 'info';
  message: string;
  time: string;
  status: 'pending' | 'handled';
};

// 报警等级映射
export const AlarmLevelMap = {
  error: '严重',
  warning: '警告',
  info: '提示'
} as const;

// 设备状态映射
export const DeviceStatusMap = {
  alarm: '报警',
  warning: '预警',
  normal: '正常',
  offline: '离线',
  shutdown: '停机'
} as const;

// 停机记录类型
export type ShutdownRecord = {
  id: string;
  deviceId: string;
  deviceName: string;
  startTime: string;
  endTime: string;
  duration: number;
  reason: string;
  operator: string;
};

// 系统日志类型
export type SystemLog = {
  id: string;
  type: 'info' | 'warning' | 'error';
  message: string;
  timestamp: string;
  operator: string;
};

// 用户信息类型
export type UserInfo = {
  id: string;
  username: string;
  role: 'admin' | 'operator' | 'viewer';
  lastLogin: string;
  status: 'active' | 'inactive';
};

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
} 
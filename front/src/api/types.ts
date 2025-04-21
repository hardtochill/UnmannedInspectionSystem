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

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
} 
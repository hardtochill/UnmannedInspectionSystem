// 模拟接口数据
import { ref } from 'vue';
import type { DeviceStatus, AlarmRecord } from './types';

interface AlarmStatistics {
  type: string;
  count: number;
}

interface MapData {
  // 地图数据接口定义
  [key: string]: any;
}

interface DashboardData {
  alarmStatistics: AlarmStatistics[];
  mapData: MapData;
}

interface AlarmDisposal {
  waiting: number;
  completed: number;
}

interface DeviceInfo {
  detected: number;
  followed: number;
}

interface OperatingStatus {
  stopped: number;
  running: number;
}

interface PendingAlarm {
  id: number;
  type: string;
  time: string;
}

interface AlarmTrend {
  month: string;
  count: number;
}

interface WorkspaceData {
  alarmDevices: number;
  alarmDisposal: AlarmDisposal;
  deviceInfo: DeviceInfo;
  operatingStatus: OperatingStatus;
  pendingAlarms: PendingAlarm[];
  alarmTrend: AlarmTrend[];
}

// 首页数据大屏模拟数据
const getDashboardData = (): DashboardData => {
  return {
    alarmStatistics: [
      { type: '管道泄漏', count: 10 },
      { type: '液面检测', count: 5 },
      { type: '数字仪表检测', count: 8 },
      { type: '状态监测', count: 12 },
      { type: '指针仪表检测', count: 6 }
    ],
    mapData: { /* 地图数据 */ }
  };
};

// 工作台模拟数据
const getWorkspaceData = (): WorkspaceData => {
  return {
    alarmDevices: 20,
    alarmDisposal: { waiting: 15, completed: 5 },
    deviceInfo: { detected: 30, followed: 10 },
    operatingStatus: { stopped: 8, running: 12 },
    pendingAlarms: [
      { id: 1, type: '管道泄漏', time: '2025-04-18 10:00' },
      { id: 2, type: '液面检测', time: '2025-04-18 10:30' }
    ],
    alarmTrend: [
      { month: '2025-01', count: 20 },
      { month: '2025-02', count: 25 },
      { month: '2025-03', count: 30 }
    ]
  };
};

// 模拟设备列表数据
const mockDeviceStatus: DeviceStatus[] = [
  {
    id: 'device-001',
    name: '水泵 #1',
    status: 'normal',
    location: '一号车间',
    lastUpdate: '2024-12-04 16:30:00',
    type: '水泵',
    description: '主要供水设备',
    imageUrl: '/images/devices/pump-1.jpg',
    tags: ['重要', '自动化']
  },
  {
    id: 'device-002',
    name: '联轴器 #1',
    status: 'alarm',
    location: '二号车间',
    lastUpdate: '2024-12-04 16:25:00',
    type: '联轴器',
    description: '传动装置',
    imageUrl: '/images/devices/coupling-1.jpg',
    tags: ['关键', '维护']
  },
  {
    id: 'device-003',
    name: 'EDI装置 #1',
    status: 'warning',
    location: '纯水车间',
    lastUpdate: '2024-12-04 16:20:00',
    type: 'EDI装置',
    description: '水处理设备',
    imageUrl: '/images/devices/edi-1.jpg',
    tags: ['水处理', '自动化']
  }
];

// 模拟报警记录数据
const mockAlarmRecords: AlarmRecord[] = [
  {
    id: 'alarm-001',
    deviceId: 'device-002',
    deviceName: '联轴器 #1',
    level: 'error',
    message: '设备温度超过阈值（85℃）',
    time: '2024-12-04 16:15:00',
    status: 'pending'
  },
  {
    id: 'alarm-002',
    deviceId: 'device-003',
    deviceName: 'EDI装置 #1',
    level: 'warning',
    message: '设备振动异常',
    time: '2024-12-04 16:10:00',
    status: 'pending'
  }
];

// 获取设备列表
export const getDeviceList = () => {
  const statusOrder = {
    'alarm': 0,
    'warning': 1,
    'normal': 2,
    'offline': 3,
    'shutdown': 4
  };

  return mockDeviceStatus.sort((a, b) => {
    return statusOrder[a.status] - statusOrder[b.status];
  });
};

// 获取设备详情
export const getDeviceDetail = (id: string) => {
  return mockDeviceStatus.find(device => device.id === id);
};

// 获取设备报警记录
export const getDeviceAlarms = (deviceId: string) => {
  return mockAlarmRecords.filter(alarm => alarm.deviceId === deviceId);
};

// 处理报警
export const handleAlarm = (alarmId: string) => {
  const alarm = mockAlarmRecords.find(a => a.id === alarmId);
  if (alarm) {
    alarm.status = 'handled';
  }
  return alarm;
};

// 导出模拟接口函数
export { getDashboardData, getWorkspaceData };
export type { DashboardData, WorkspaceData, AlarmStatistics, MapData, AlarmDisposal, DeviceInfo, OperatingStatus, PendingAlarm, AlarmTrend, DeviceStatus, AlarmRecord }; 
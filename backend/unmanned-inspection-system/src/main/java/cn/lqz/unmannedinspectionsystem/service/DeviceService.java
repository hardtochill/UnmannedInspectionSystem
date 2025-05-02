package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.vo.DeviceStatusCountVO;

public interface DeviceService {
    /**
     * 统计设备数
     * @return
     */
    DeviceStatusCountVO countStatus();
}

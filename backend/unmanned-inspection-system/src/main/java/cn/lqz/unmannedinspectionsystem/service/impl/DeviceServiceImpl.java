package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.AlarmStatusEnum;
import cn.lqz.unmannedinspectionsystem.enums.DeviceRunStatusEnum;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.mapper.DeviceMapper;
import cn.lqz.unmannedinspectionsystem.pojo.vo.DeviceStatusCountVO;
import cn.lqz.unmannedinspectionsystem.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    private final DeviceMapper deviceMapper;
    private final AlarmMapper alarmMapper;
    /**
     * 统计设备状态数
     * @return
     */
    @Override
    public DeviceStatusCountVO countStatus() {
        // 运行中设备数
        Integer runningCount = deviceMapper.countByStatus(DeviceRunStatusEnum.RUNNING.getStatus());
        // 报警设备数
        Integer alarmCount = alarmMapper.countDeviceByStatus(AlarmStatusEnum.UNPROCESSED.getStatus());
        return new DeviceStatusCountVO(runningCount,alarmCount);
    }
}

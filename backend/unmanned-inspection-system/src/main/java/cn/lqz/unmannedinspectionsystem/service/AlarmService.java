package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;

public interface AlarmService {
    /**
     * 报警记录分页查询
     * @param alarmPageQueryDTO
     * @return
     */
    PageResultVO loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO);
}

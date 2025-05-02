package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;

public interface AlarmService {
    /**
     * 报警记录分页查询
     * @param alarmPageQueryDTO
     * @return
     */
    PageResultVO loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO);

    /**
     * 标记已处理
     * @param alarmId
     */
    void changeToProcessed(Long alarmId);

    /**
     * 统计处理状态
     * @return
     */
    AlarmStatusCountVO countStatus();
}

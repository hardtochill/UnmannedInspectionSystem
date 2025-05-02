package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.AlarmStatusEnum;
import cn.lqz.unmannedinspectionsystem.enums.MeasuringPointStatusEnum;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Alarm;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.service.AlarmService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmMapper alarmMapper;
    /**
     * 报警记录分页查询
     * @param alarmPageQueryDTO
     * @return
     */
    @Override
    public PageResultVO loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO) {
        log.info("报警记录分页查询：{}",alarmPageQueryDTO);
        PageHelper.startPage(alarmPageQueryDTO.getPageNo(), alarmPageQueryDTO.getPageSize());
        Page<AlarmVO> alarmPage =  alarmMapper.findBatch(alarmPageQueryDTO);
        // 根据报警类型int值填入报警类型String值，异常测点的状态就是报警的类型
        for (AlarmVO alarmVO : alarmPage) {
            MeasuringPointStatusEnum mpse = MeasuringPointStatusEnum.getByStatus(alarmVO.getType());
            alarmVO.setTypeString(mpse==null?"无":mpse.getDescription());
        }
        return new PageResultVO(alarmPageQueryDTO.getPageNo(), alarmPageQueryDTO.getPageSize(), alarmPage.getTotal(), alarmPage.getResult());
    }

    /**
     * 标记已处理
     * @param alarmId
     */
    @Override
    public void changeToProcessed(Long alarmId) {
        log.info("报警记录：标记已处理：{}",alarmId);
        Alarm alarm = new Alarm();
        alarm.setAlarmId(alarmId);
        alarm.setStatus(AlarmStatusEnum.PROCESSED.getStatus());
        alarmMapper.update(alarm);
    }

    /**
     * 统计处理状态
     * @return
     */
    @Override
    public AlarmStatusCountVO countStatus() {
        Long unprocessedCount = alarmMapper.countByStatus(AlarmStatusEnum.UNPROCESSED.getStatus());
        Long processedCount = alarmMapper.countByStatus(AlarmStatusEnum.PROCESSED.getStatus());
        return new AlarmStatusCountVO(unprocessedCount,processedCount);
    }
}

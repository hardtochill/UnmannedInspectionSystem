package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.MeasuringPointStatusEnum;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
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
        // 根据报警类型int值填入报警类型String值
        for (AlarmVO alarmVO : alarmPage) {
            alarmVO.setTypeString(MeasuringPointStatusEnum.getByStatus(alarmVO.getType()).getDescription());
        }
        return new PageResultVO(alarmPageQueryDTO.getPageNo(), alarmPageQueryDTO.getPageSize(), alarmPage.getTotal(), alarmPage.getResult());
    }
}

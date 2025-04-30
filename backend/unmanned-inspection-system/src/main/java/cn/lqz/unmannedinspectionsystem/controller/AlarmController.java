package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.annotations.PageQueryAutoFill;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.AlarmService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @PostMapping("/loadAlarmList")
    @PageQueryAutoFill
    public ResponseVO<PageResultVO> loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO){
        return ResponseUtils.success(alarmService.loadAlarmList(alarmPageQueryDTO));
    }
}

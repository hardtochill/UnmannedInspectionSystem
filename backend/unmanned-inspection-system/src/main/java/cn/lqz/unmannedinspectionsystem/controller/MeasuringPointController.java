package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.MeasuringPointService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp")
@RequiredArgsConstructor
public class MeasuringPointController {
    private final MeasuringPointService measuringPointService;

    @PostMapping("/loadList")
    public ResponseVO<PageResultVO> loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO){
        return ResponseUtils.success(measuringPointService.loadMeasuringPointList(measuringPointPageQueryDTO));
    }
}

package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.mapper.MeasuringPointMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.service.MeasuringPointService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeasuringPointServiceImpl implements MeasuringPointService {
    private final MeasuringPointMapper measuringPointMapper;
    @Override
    public PageResultVO loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO) {
        log.info("分页查询测点记录：{}",measuringPointPageQueryDTO);
        PageHelper.startPage(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize());
        Page<MeasuringPointVO> measuringPointVOPage = measuringPointMapper.findByStatus(measuringPointPageQueryDTO);
        return new PageResultVO(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize(),measuringPointVOPage.getTotal(),measuringPointVOPage.getResult());
    }
}

package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.entity.MeasuringPoint;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeasuringPointMapper {

    /**
     * 新增
     * @param measuringPoint
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(MeasuringPoint measuringPoint);

    /**
     * 修改
     * @param measuringPoint
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(MeasuringPoint measuringPoint);
}

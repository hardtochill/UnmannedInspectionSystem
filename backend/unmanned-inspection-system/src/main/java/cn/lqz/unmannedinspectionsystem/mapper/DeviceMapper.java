package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper {
    /**
     * 插入
     * @param device
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(Device device);

    /**
     * 修改
     * @param device
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(Device device);
}

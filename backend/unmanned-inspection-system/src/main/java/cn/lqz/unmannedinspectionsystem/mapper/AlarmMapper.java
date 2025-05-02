package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Alarm;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AlarmMapper {
    /**
     * 新增
     * @param alarm
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(Alarm alarm);

    /**
     * 修改
     * @param alarm
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(Alarm alarm);


    /**
     * 批量查询
     * @param alarmPageQueryDTO
     * @return
     */
    Page<AlarmVO> findBatch(AlarmPageQueryDTO alarmPageQueryDTO);

    /**
     * 统计状态
     * @param status
     * @return
     */
    @Select("select count(*) from alarm where status=#{status}")
    Long countByStatus(Integer status);

    /**
     * 统计不同报警状态的设备数量
     * @param status
     * @return
     */
    @Select("select count(device_id) from alarm where status=#{status}")
    Integer countDeviceByStatus(Integer status);
}

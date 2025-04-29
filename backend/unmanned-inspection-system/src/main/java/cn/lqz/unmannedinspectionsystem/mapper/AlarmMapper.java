package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Alarm;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

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
}

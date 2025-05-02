package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusCountVO {
    /**
     * 运行中设备数
     */
    private Integer runningCount;
    /**
     * 报警设备数
     */
    private Integer alarmCount;
}

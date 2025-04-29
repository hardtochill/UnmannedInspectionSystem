package cn.lqz.unmannedinspectionsystem.enums;

import cn.lqz.unmannedinspectionsystem.pojo.entity.MeasuringPoint;

/**
 * 测点状态枚举
 */
public enum MeasuringPointStatusEnum {
    ;
    private Integer status;
    private String description;
    MeasuringPointStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }

    public static MeasuringPointStatusEnum getByStatus(Integer status){
        for (MeasuringPointStatusEnum measuringPointStatusEnum : MeasuringPointStatusEnum.values()) {
            if(measuringPointStatusEnum.status.equals(status)){
                return measuringPointStatusEnum;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}

package cn.lqz.unmannedinspectionsystem.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class YoloConfig {
    @Value("${service.yolo.input-image-filepath}")
    // 入参图片文件夹
    private String inputImageFolder;
    @Value("${service.yolo.output-image-filepath}")
    // 出参图片文件夹
    private String outputImageFolder;
}

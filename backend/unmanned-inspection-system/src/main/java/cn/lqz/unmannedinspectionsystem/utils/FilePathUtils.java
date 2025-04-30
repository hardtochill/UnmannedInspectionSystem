package cn.lqz.unmannedinspectionsystem.utils;

import cn.lqz.unmannedinspectionsystem.config.YoloConfig;
import cn.lqz.unmannedinspectionsystem.constants.FileConstants;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 文件路径工具类
 */
public class FilePathUtils {

    /**
     * 生成yolo入参图片路径
     * @param foldPath
     * @param id
     * @return
     */
    public static String generateYoloInputImageFilePath(String foldPath,String id){
        return foldPath + FileConstants.PATH_SEPARATOR + id+FileConstants.IMAGE_SUFFIX;
    }

    /**
     * 生成yolo出参图片路径
     * @param foldPath
     * @param id
     * @return
     */
    public static String generateYoloOutputImageFilePath(String foldPath,String id){
        return foldPath + FileConstants.PATH_SEPARATOR + id+FileConstants.IMAGE_SUFFIX;
    }
}

package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.config.YoloConfig;
import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.service.DetectService;
import cn.lqz.unmannedinspectionsystem.utils.FilePathUtils;
import cn.lqz.unmannedinspectionsystem.utils.YoloUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectServiceImpl implements DetectService {
    private final YoloConfig yoloConfig;
    /**
     * 图片检测
     * @param image
     */
    @Override
    public void detect(MultipartFile image) {
        log.info("图片检测");
        // 图片目录生成
        File yoloInputImageFileFolder = new File(yoloConfig.getInputImageFolder());
        if(!yoloInputImageFileFolder.exists()){
            yoloInputImageFileFolder.mkdirs();
        }
        // 将图片存到本地
        UUID uuid = UUID.randomUUID();
        File inputFile = new File(FilePathUtils.generateYoloInputImageFilePath(yoloConfig.getInputImageFolder(),uuid.toString()));
        try{
            image.transferTo(inputFile);
        }catch (Exception e){
            log.error("图片输入异常");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }

        // python脚本调用，得到输出文件的路径
        String outputFilePath = YoloUtils.execute(yoloConfig.getOutputImageFolder(),uuid.toString(),inputFile.getAbsolutePath());

        // 读取图片
        File outputFile = new File(outputFilePath);
        // 文件流返回
        // 1.获取请求的输出流
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        response.setContentType("application/x-msdownload;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;");
        response.setContentLengthLong(outputFile.length());
        try(
                FileInputStream outputFileInputStream = new FileInputStream(outputFile);
                ServletOutputStream responseOutputStream = response.getOutputStream()
        ){
            // 2.字节流拷贝返回
            // 缓冲区
            byte[] byteData = new byte[1024];
            int length;
            while((length=outputFileInputStream.read(byteData))!=-1){
                responseOutputStream.write(byteData,0,length);
            }
            responseOutputStream.flush();
        }catch (Exception e){
            log.error("图片输出异常");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }
}

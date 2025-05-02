package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.mapper.MeasuringPointMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.service.MeasuringPointService;
import cn.lqz.unmannedinspectionsystem.utils.FilePathUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeasuringPointServiceImpl implements MeasuringPointService {
    private final MeasuringPointMapper measuringPointMapper;
    @Value("${service.mp.image-folder-path}")
    private String mpImageFolderPath;

    /**
     * 测点分页查询
     * @param measuringPointPageQueryDTO
     * @return
     */
    @Override
    public PageResultVO loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO) {
        log.info("分页查询测点记录：{}",measuringPointPageQueryDTO);
        PageHelper.startPage(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize());
        Page<MeasuringPointVO> measuringPointVOPage = measuringPointMapper.findByStatus(measuringPointPageQueryDTO);
        // 记载测点图片
        // 测点图片文件夹
        File folder = new File(mpImageFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        try{
            for (MeasuringPointVO measuringPointVO : measuringPointVOPage) {
                // 测点图片文件
                File file = new File(FilePathUtils.generateMpImageFilePath(mpImageFolderPath, measuringPointVO.getMpId()));
                if(!file.exists()){
                    continue;
                }
                BufferedImage bufferedImage = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                measuringPointVO.setBase64Image(Base64.getEncoder().encodeToString(imageBytes));
            }
        }catch (Exception e){
            log.error("加载测点图片失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
        return new PageResultVO(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize(),measuringPointVOPage.getTotal(),measuringPointVOPage.getResult());
    }

    /**
     * 测点图片
     * @param mpId
     */
    @Override
    public String loadImage(Long mpId) {
        log.info("获取测点图片：{}",mpId);
        // 测点图片文件夹
        File folder = new File(mpImageFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        // 测点图片文件
        File file = new File(FilePathUtils.generateMpImageFilePath(mpImageFolderPath,mpId));
        // 转Base64返回
        try{
            BufferedImage bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }catch (Exception e){
            log.error("加载测点图片失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }
}

package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.service.DetectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/detect")
@RequiredArgsConstructor
public class DetectController {
    private final DetectService detectService;

    @PostMapping("/detect")
    public void imageUpload(MultipartFile image){
        detectService.detect(image);
    }
}

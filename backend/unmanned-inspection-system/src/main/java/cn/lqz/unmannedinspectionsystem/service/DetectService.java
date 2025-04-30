package cn.lqz.unmannedinspectionsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface DetectService {

    void detect(MultipartFile image);
}

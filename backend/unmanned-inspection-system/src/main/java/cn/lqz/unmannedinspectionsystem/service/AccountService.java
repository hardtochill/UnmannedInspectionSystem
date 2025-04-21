package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;

public interface AccountService {
    /**
     * 登录
     * @param loginDTO
     * @return
     */
    UserTokenDTO login(LoginDTO loginDTO);
}

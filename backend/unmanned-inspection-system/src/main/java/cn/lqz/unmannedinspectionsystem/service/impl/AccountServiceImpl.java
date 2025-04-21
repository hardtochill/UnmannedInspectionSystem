package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.enums.UserRoleTypeEnum;
import cn.lqz.unmannedinspectionsystem.enums.UserStatusEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.mapper.UserMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.service.AccountService;
import cn.lqz.unmannedinspectionsystem.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserMapper userMapper;
    /**
     * 登录
     * @param loginDTO
     * @return
     */
    @Override
    public UserTokenDTO login(LoginDTO loginDTO) {
        log.info("用户登录：{}",loginDTO);

        // 1.用户是否存在
        User user = userMapper.findByPhoneNumber(loginDTO.getPhoneNumber());
        if(null==user){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户不存在");
        }
        // 2.密码是否正确
        if(!user.getPassword().equals(loginDTO.getPassword())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"账号或密码错误");
        }
        // 3.用户是否被禁用
        if(!UserStatusEnum.ENABLE.getStatus().equals(user.getStatus())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户已被禁用");
        }
        // 4.用户是否有权登录
        if(!UserRoleTypeEnum.ADMIN.getType().equals(user.getRoleType())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户无权登录");
        }
        // todo 5.查redis，看用户是否已在线
        String token = StringUtils.getUserToken(user.getUserId());

        // 6.生成tokenDTO
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        BeanUtils.copyProperties(user,userTokenDTO);
        userTokenDTO.setToken(token);
        // todo 7.存入redis

        return userTokenDTO;
    }
}

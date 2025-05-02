package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.UserStatusEnum;
import cn.lqz.unmannedinspectionsystem.mapper.UserMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.UserVO;
import cn.lqz.unmannedinspectionsystem.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${service.default-password}")
    private String defaultPassword;

    private final UserMapper userMapper;
    @Override
    public PageResultVO loadUserList(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPageNo(),userPageQueryDTO.getPageSize());
        Page<UserVO> userVOPage = userMapper.findBatch(userPageQueryDTO);
        return new PageResultVO(userPageQueryDTO.getPageNo(),userPageQueryDTO.getPageSize(),userVOPage.getTotal(),userVOPage.getResult());
    }

    @Override
    public void createOrUpdate(User user) {
        log.info("用户新增或修改：{}",user);
        // 新增
        if(null == user.getUserId()){
            user.setPassword(defaultPassword);
            user.setStatus(UserStatusEnum.ENABLE.getStatus());
            userMapper.insert(user);
        }else{ // 修改
            userMapper.update(user);
        }
    }


}

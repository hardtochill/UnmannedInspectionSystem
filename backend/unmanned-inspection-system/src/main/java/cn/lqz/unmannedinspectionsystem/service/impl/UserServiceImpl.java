package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.mapper.UserMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.UserVO;
import cn.lqz.unmannedinspectionsystem.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Override
    public PageResultVO loadUserList(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPageNo(),userPageQueryDTO.getPageSize());
        Page<UserVO> userVOPage = userMapper.findBatch(userPageQueryDTO);
        return new PageResultVO(userPageQueryDTO.getPageNo(),userPageQueryDTO.getPageSize(),userVOPage.getTotal(),userVOPage.getResult());
    }
}

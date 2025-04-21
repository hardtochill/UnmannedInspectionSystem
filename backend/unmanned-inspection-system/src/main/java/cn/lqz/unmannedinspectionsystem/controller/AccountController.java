package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.AccountService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/login")
    public ResponseVO<UserTokenDTO> login(@Valid LoginDTO loginDTO){
        return ResponseUtils.success(accountService.login(loginDTO));
    }
}

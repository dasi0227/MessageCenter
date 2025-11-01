package com.dasi.core.controller;

import com.dasi.pojo.dto.UserDTO;
import com.dasi.common.result.Result;
import com.dasi.core.service.UserService;
import com.dasi.pojo.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO) {
        UserVO userVO = userService.login(userDTO);
        return Result.success(userVO);
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String newToken = userService.refresh(request);
        return Result.success(newToken);
    }
}

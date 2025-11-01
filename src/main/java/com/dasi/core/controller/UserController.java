package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.PasswordDTO;
import com.dasi.pojo.dto.StatusDTO;
import com.dasi.pojo.dto.LoginDTO;
import com.dasi.common.result.Result;
import com.dasi.core.service.UserService;
import com.dasi.pojo.dto.UserPageDTO;
import com.dasi.pojo.vo.UserLoginVO;
import com.dasi.pojo.vo.UserPageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody LoginDTO loginDTO) {
        userService.register(loginDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserLoginVO userLoginVO = userService.login(loginDTO);
        return Result.success(userLoginVO);
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String newToken = userService.refresh(request);
        return Result.success(newToken);
    }

    @GetMapping
    public Result<PageResult<UserPageVO>> getUsers(@Valid @RequestBody UserPageDTO userPageDTO) {
        PageResult<UserPageVO> pageResult = userService.getUsers(userPageDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        userService.updatePassword(passwordDTO);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO statusDTO) {
        userService.updateStatus(statusDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeUser(@PathVariable("id") Long id) {
        userService.removeUser(id);
        return Result.success();
    }
}

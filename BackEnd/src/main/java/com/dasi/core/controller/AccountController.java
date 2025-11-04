package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.AccountPasswordDTO;
import com.dasi.pojo.dto.AccountLoginDTO;
import com.dasi.common.result.Result;
import com.dasi.core.service.AccountService;
import com.dasi.pojo.dto.AccountPageDTO;
import com.dasi.pojo.vo.AccountLoginVO;
import com.dasi.pojo.vo.AccountPageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody AccountLoginDTO dto) {
        accountService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<AccountLoginVO> login(@Valid @RequestBody AccountLoginDTO dto) {
        AccountLoginVO vo = accountService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String newToken = accountService.refresh(request);
        return Result.success(newToken);
    }

    @GetMapping("/page")
    public Result<PageResult<AccountPageVO>> getAccountPage(@Valid @RequestBody AccountPageDTO accountPageDTO) {
        PageResult<AccountPageVO> pageResult = accountService.getAccountPage(accountPageDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody AccountPasswordDTO accountPasswordDTO) {
        accountService.updatePassword(accountPasswordDTO);
        return Result.success();
    }

    @DeleteMapping("/remove/{id}")
    public Result<Void> removeUser(@PathVariable("id") Long id) {
        accountService.removeAccount(id);
        return Result.success();
    }
}

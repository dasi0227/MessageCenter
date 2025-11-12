package com.dasi.core.controller;

import com.dasi.common.annotation.RateLimit;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.AccountAddDTO;
import com.dasi.pojo.dto.AccountUpdateDTO;
import com.dasi.pojo.dto.AccountLoginDTO;
import com.dasi.common.result.Result;
import com.dasi.core.service.AccountService;
import com.dasi.pojo.dto.AccountPageDTO;
import com.dasi.pojo.entity.Account;
import com.dasi.pojo.vo.AccountLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@SuppressWarnings("unused")
@Validated
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    @RateLimit(
            key = "T(com.dasi.util.IpUtil).getClientIP(#request)",
            limit = 3,
            ttl = 3600,
            message = "同一IP一小时内不允许注册超过 3 次，请过一会儿后重新尝试！"
    )
    public Result<Void> register(@Valid @RequestBody AccountLoginDTO dto, HttpServletRequest request) {
        accountService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    @RateLimit(
            key = "#dto.name",
            limit = 5,
            ttl = 60,
            message = "同一用户一分钟内不允许登陆超过 5 次，请过一会儿后重新尝试！"
    )
    public Result<AccountLoginVO> login(@Valid @RequestBody AccountLoginDTO dto) {
        AccountLoginVO vo = accountService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String newToken = accountService.refresh(request);
        return Result.success(newToken);
    }

    @PostMapping("/page")
    public Result<PageResult<Account>> getAccountPage(@Valid @RequestBody AccountPageDTO accountPageDTO) {
        PageResult<Account> pageResult = accountService.getAccountPage(accountPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Account>> getAccountList() {
        List<Account> result = accountService.getAccountList();
        return Result.success(result);
    }

    @GetMapping("/role")
    public Result<List<String>> getAccountRole() {
        List<String> list = AccountRole.getAccountRoleList();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Void> addAccount(@Valid @RequestBody AccountAddDTO dto) {
        accountService.addAccount(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateAccount(@Valid @RequestBody AccountUpdateDTO accountUpdateDTO) {
        accountService.updateAccount(accountUpdateDTO);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeUser(@PathVariable("id") Long id) {
        accountService.removeAccount(id);
        return Result.success();
    }
}

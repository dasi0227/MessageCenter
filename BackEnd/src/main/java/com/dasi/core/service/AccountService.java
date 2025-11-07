package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.AccountAddDTO;
import com.dasi.pojo.dto.AccountUpdateDTO;
import com.dasi.pojo.dto.AccountLoginDTO;
import com.dasi.pojo.dto.AccountPageDTO;
import com.dasi.pojo.entity.Account;
import com.dasi.pojo.vo.AccountLoginVO;
import com.dasi.pojo.vo.AccountPageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface AccountService extends IService<Account> {
    void register(AccountLoginDTO dto);

    AccountLoginVO login(AccountLoginDTO dto);

    String refresh(HttpServletRequest request);

    PageResult<AccountPageVO> getAccountPage(AccountPageDTO dto);

    void updateAccount(AccountUpdateDTO dto);

    void removeAccount(Long id);

    List<String> getAccountRole();

    void addAccount(@Valid AccountAddDTO dto);
}

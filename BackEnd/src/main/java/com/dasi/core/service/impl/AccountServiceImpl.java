package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.exception.*;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.AccountMapper;
import com.dasi.core.service.AccountService;
import com.dasi.pojo.dto.AccountAddDTO;
import com.dasi.pojo.dto.AccountUpdateDTO;
import com.dasi.pojo.dto.AccountLoginDTO;
import com.dasi.pojo.dto.AccountPageDTO;
import com.dasi.pojo.entity.Account;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.pojo.vo.AccountLoginVO;
import com.dasi.pojo.vo.AccountPageVO;
import com.dasi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    @AutoFill(FillType.INSERT)
    public void register(AccountLoginDTO dto) {
        // 检查重名
        if (exists(new LambdaQueryWrapper<Account>().eq(Account::getName, dto.getName()))) {
            throw new AccountException(ResultInfo.ACCOUNT_ALREADY_EXIST);
        }

        // 构建账户
        Account account = BeanUtil.copyProperties(dto, Account.class);
        account.setPassword(SecureUtil.md5(dto.getPassword()));
        account.setRole(AccountRole.USER);
        save(account);

        log.debug("【Account Service】注册账户：{}", dto);
    }

    @Override
    public AccountLoginVO login(AccountLoginDTO dto) {
        // 查询账户
        Account account = getOne(new LambdaQueryWrapper<Account>().eq(Account::getName, dto.getName()), false);
        if (account == null) {
            throw new AccountException(ResultInfo.ACCOUNT_NOT_FOUND);
        }

        // 校验密码
        String password = SecureUtil.md5(dto.getPassword());
        if (!password.equals(account.getPassword())) {
            throw new AccountException(ResultInfo.ACCOUNT_PASSWORD_ERROR);
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getAccountIdClaimKey(), account.getId());
        claims.put(jwtProperties.getAccountRoleClaimKey(), account.getRole());
        String token = jwtUtil.createToken(claims);

        // 构造视图
        AccountLoginVO vo = BeanUtil.copyProperties(account, AccountLoginVO.class);
        vo.setToken(token);

        log.debug("【Account Service】账户登陆：{}", dto);
        return vo;
    }

    @Override
    public String refresh(HttpServletRequest request) {
        String oldToken = request.getHeader(jwtProperties.getAccountTokenName());
        String newToken = jwtUtil.refreshToken(oldToken);

        log.debug("【Account Service】刷新 Token ：{}", newToken);
        return newToken;
    }

    @Override
    public PageResult<AccountPageVO> getAccountPage(AccountPageDTO dto) {
        // 分页参数
        Page<Account> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 查询条件
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<Account>()
                .like(StrUtil.isNotBlank(dto.getName()), Account::getName, dto.getName())
                .eq(dto.getRole() != null, Account::getRole, dto.getRole())
                .orderByDesc(Account::getCreatedAt);

        // 分页查询
        Page<Account> result = page(param, wrapper);

        log.debug("【Account Service】分页查询账户：{}", dto);
        return PageResult.of(result, AccountPageVO.class);
    }

    @Override
    @AutoFill(FillType.UPDATE)
    @AdminOnly
    public void updateAccount(AccountUpdateDTO dto) {
        if (AccountContextHolder.get().getId().equals(dto.getId())) {
            throw new AccountException(ResultInfo.ACCOUNT_REMOVE_UPDATE_DENIED);
        }
        if (!update(new LambdaUpdateWrapper<Account>()
                .eq(Account::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Account::getName, dto.getName())
                .set(dto.getPassword() != null, Account::getPassword, SecureUtil.md5(dto.getPassword()))
                .set(Account::getRole, dto.getRole())
                .set(Account::getUpdatedAt, dto.getUpdatedAt()))) {
            throw new AccountException(ResultInfo.ACCOUNT_UPDATE_ERROR);
        }
        log.debug("【Account Service】更新账户：{}", dto);
    }

    @AdminOnly
    @Override
    public void removeAccount(Long id) {
        if (AccountContextHolder.get().getId().equals(id)) {
            throw new AccountException(ResultInfo.ACCOUNT_REMOVE_UPDATE_DENIED);
        }
        if (getById(id).getRole().equals(AccountRole.ADMIN)) {
            throw new AccountException(ResultInfo.ACCOUNT_REMOVE_DENIED);
        }
        if (!removeById(id)) {
            throw new AccountException(ResultInfo.ACCOUNT_REMOVE_ERROR);
        }
        log.debug("【Account Service】删除账户：{}", id);
    }

    @Override
    public List<String> getAccountRole() {
        log.debug("【Account Service】查询账户角色");
        return Arrays.stream(AccountRole.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    public void addAccount(AccountAddDTO dto) {
        // 检查重名
        if (exists(new LambdaQueryWrapper<Account>().eq(Account::getName, dto.getName()))) {
            throw new AccountException(ResultInfo.ACCOUNT_ALREADY_EXIST);
        }

        // 构建联系人
        Account account = BeanUtil.copyProperties(dto, Account.class);
        account.setPassword(SecureUtil.md5(dto.getPassword()));
        save(account);

        log.debug("【Account Service】新增账户：{}", dto);
    }
}
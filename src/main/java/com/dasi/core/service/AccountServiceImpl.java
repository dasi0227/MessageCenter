package com.dasi.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.exception.*;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.AccountMapper;
import com.dasi.pojo.dto.AccountPasswordDTO;
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

import java.util.HashMap;
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
            throw new AccountException(ResultInfo.ACCOUNT_NOT_EXIST);
        }

        // 校验密码
        String password = SecureUtil.md5(dto.getPassword());
        log.debug("dto password：{}", password);
        log.debug("account password：{}", account.getPassword());
        if (!password.equals(account.getPassword())) {
            throw new AccountException(ResultInfo.ACCOUNT_PASSWORD_ERROR);
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getClaimAccountId(), account.getId());
        claims.put(jwtProperties.getClaimAccountRole(), account.getRole());
        String token = jwtUtil.createToken(claims);

        // 构造视图
        AccountLoginVO vo = BeanUtil.copyProperties(account, AccountLoginVO.class);
        vo.setToken(token);

        log.debug("【Account Service】账户登陆：{}", dto);
        return vo;
    }

    @Override
    public String refresh(HttpServletRequest request) {
        String oldToken = request.getHeader(jwtProperties.getTokenName());
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
                .orderByDesc(Account::getCreatedAt);

        // 分页查询
        Page<Account> result = page(param, wrapper);

        log.debug("【Account Service】分页查询账户：{}", dto);
        return PageResult.of(result, AccountPageVO.class);
    }

    @Override
    @AdminOnly
    public void updatePassword(AccountPasswordDTO dto) {
        if (!update(new LambdaUpdateWrapper<Account>()
                .eq(Account::getId, dto.getId())
                .set(Account::getPassword, SecureUtil.md5(dto.getPassword())))) {
            throw new AccountException(ResultInfo.ACCOUNT_UPDATE_ERROR);
        }
        log.debug("【Account Service】更新账户密码：{}", dto);
    }

    @AdminOnly
    @Override
    public void removeAccount(Long id) {
        if (!removeById(id)) {
            throw new AccountException(ResultInfo.ACCOUNT_REMOVE_ERROR);
        }
        log.debug("【Account Service】删除账户：{}", id);
    }
}
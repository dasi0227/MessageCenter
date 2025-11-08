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
import com.dasi.common.annotation.UniqueField;
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
import com.dasi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    @AutoFill(FillType.INSERT)
    @UniqueField(serviceClass = AccountServiceImpl.class, fieldName = "name", resultInfo = ResultInfo.ACCOUNT_NAME_ALREADY_EXISTS)
    public void register(AccountLoginDTO dto) {
        Account account = BeanUtil.copyProperties(dto, Account.class);
        account.setPassword(SecureUtil.md5(dto.getPassword()));
        account.setRole(AccountRole.USER);
        boolean flag = save(account);

        if (!flag) {
            log.warn("【Account Service】注册失败：{}", dto);
        }
    }

    @Override
    public AccountLoginVO login(AccountLoginDTO dto) {
        Account account = getOne(new LambdaQueryWrapper<Account>().eq(Account::getName, dto.getName()), false);
        if (account == null) {
            throw new MessageCenterException(ResultInfo.ACCOUNT_NOT_FOUND);
        }

        String password = SecureUtil.md5(dto.getPassword());
        if (!password.equals(account.getPassword())) {
            throw new MessageCenterException(ResultInfo.ACCOUNT_PASSWORD_ERROR);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getAccountIdClaimKey(), account.getId());
        claims.put(jwtProperties.getAccountRoleClaimKey(), account.getRole());
        String token = jwtUtil.createToken(claims);

        AccountLoginVO vo = BeanUtil.copyProperties(account, AccountLoginVO.class);
        vo.setToken(token);

        log.debug("【Account Service】账户登录成功：{}", dto);
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
    public PageResult<Account> getAccountPage(AccountPageDTO dto) {
        // 分页参数
        Page<Account> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 查询条件
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<Account>()
                .like(StrUtil.isNotBlank(dto.getName()), Account::getName, dto.getName())
                .eq(dto.getRole() != null, Account::getRole, dto.getRole())
                .orderByDesc(Account::getCreatedAt);

        // 分页查询
        Page<Account> result = page(param, wrapper);

        return PageResult.of(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @UniqueField(serviceClass = AccountServiceImpl.class, fieldName = "name", resultInfo = ResultInfo.ACCOUNT_NAME_ALREADY_EXISTS)
    public void addAccount(AccountAddDTO dto) {
        Account account = BeanUtil.copyProperties(dto, Account.class);
        account.setPassword(SecureUtil.md5(dto.getPassword()));
        boolean flag = save(account);
        if (!flag) {
            log.warn("【Account Service】插入失败：{}", dto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(serviceClass = AccountServiceImpl.class, fieldName = "name", resultInfo = ResultInfo.ACCOUNT_NAME_ALREADY_EXISTS)
    public void updateAccount(AccountUpdateDTO dto) {
        if (AccountContextHolder.get().getId().equals(dto.getId())) {
            log.warn("【Account Service】更新失败，无法修改当前登录账户：{}", dto);
            throw new MessageCenterException(ResultInfo.ACCOUNT_UPDATE_ADMIN_DENIED);
        }

        Account target = getById(dto.getId());
        if (target != null && target.getRole() == AccountRole.ADMIN) {
            log.warn("【Account Service】删除失败，无法更新管理员账户：{}", dto);
            throw new MessageCenterException(ResultInfo.ACCOUNT_UPDATE_CURRENT_DENIED);
        }

        boolean flag = update(new LambdaUpdateWrapper<Account>()
                .eq(Account::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Account::getName, dto.getName())
                .set(dto.getPassword() != null, Account::getPassword, SecureUtil.md5(dto.getPassword()))
                .set(Account::getRole, dto.getRole())
                .set(Account::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Account Service】更新失败，没有记录或值无变化：{}", dto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    public void removeAccount(Long id) {
        if (AccountContextHolder.get().getId().equals(id)) {
            log.warn("【Account Service】删除失败，无法删除当前登录账户：{}", id);
            throw new MessageCenterException(ResultInfo.ACCOUNT_REMOVE_CURRENT_DENIED);
        }

        Account target = getById(id);
        if (target != null && target.getRole() == AccountRole.ADMIN) {
            log.warn("【Account Service】删除失败，无法删除管理员账户：{}", id);
            throw new MessageCenterException(ResultInfo.ACCOUNT_REMOVE_ADMIN_DENIED);
        }

        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Account Service】删除失败，账户不存在：{}", id);
        }
    }

    @Override
    public List<String> getAccountRole() {
        return Arrays.stream(AccountRole.values()).map(Enum::name).toList();
    }
}
package com.dasi.web.aspect;

import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AdminOnlyAspect {

    @Before("@within(com.dasi.common.annotation.AdminOnly) || @annotation(com.dasi.common.annotation.AdminOnly)")
    public void checkRole(JoinPoint joinPoint) {
        AccountRole role = AccountContextHolder.get().getRole();
        Long id = AccountContextHolder.get().getId();
        if (!AccountRole.ADMIN.equals(role)) {
            log.warn("【没有权限】方法={}，当前用户ID={}", joinPoint.getSignature().toShortString(), id);
            throw new PermissionException(ResultInfo.ACCOUNT_PERMISSION_DENIED);
        }
    }
}

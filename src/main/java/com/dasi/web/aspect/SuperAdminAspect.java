package com.dasi.web.aspect;

import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.PermissionException;
import com.dasi.util.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SuperAdminAspect {
    @Before("@annotation(com.dasi.common.annotation.SuperAdminOnly)")
    public void checkRole(JoinPoint joinPoint) {
        long userId = UserContextUtil.getUser();
        if (userId != SystemConstant.SUPER_ADMIN) {
            log.warn("【权限拒绝】方法={}，当前用户ID={}", joinPoint.getSignature().toShortString(), userId);
            throw new PermissionException(ResultInfo.USER_PERMISSION_DENIED);
        }
    }
}

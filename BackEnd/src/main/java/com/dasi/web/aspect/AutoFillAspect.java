package com.dasi.web.aspect;

import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.FillType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    @Before("@annotation(autoFill)")
    public void autoFill(JoinPoint joinPoint, AutoFill autoFill) {
        FillType fillType = autoFill.value();

        for (Object arg : joinPoint.getArgs()) {
            if (arg == null) continue;
            for (Field field : arg.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (!field.getType().equals(LocalDateTime.class)) continue;
                    if ("createdAt".equals(field.getName()) && fillType == FillType.INSERT) {
                        field.set(arg, LocalDateTime.now());
                    }
                    if ("updatedAt".equals(field.getName())) {
                        field.set(arg, LocalDateTime.now());
                    }
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
    }
}

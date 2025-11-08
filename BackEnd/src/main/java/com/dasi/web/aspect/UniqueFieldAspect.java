package com.dasi.web.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.exception.UniqueFieldConflictException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
@Slf4j
public class UniqueFieldAspect {

    @SuppressWarnings("unchecked")
    @Before("@annotation(uniqueField)")
    public void before(JoinPoint joinPoint, UniqueField uniqueField) {
        Object target = joinPoint.getTarget();
        if (!(target instanceof IService<?> service)) {
            log.error("【UniqueField】目标对象不是 IService 实现类，跳过唯一性校验: {}", target.getClass().getName());
            return;
        }

        Object dto = joinPoint.getArgs()[0];
        Object fieldValue = tryGetFieldValue(dto, uniqueField.fieldName());
        Object idValue = tryGetFieldValue(dto, uniqueField.idName());

        QueryWrapper wrapper = new QueryWrapper<>()
                .eq(fieldValue != null, uniqueField.fieldName(), fieldValue)
                .ne(idValue != null, uniqueField.idName(), idValue);
        long count = service.count(wrapper);

        if (count > 0) {
            throw new UniqueFieldConflictException(uniqueField.resultInfo());
        }
    }

    private Object tryGetFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }
}

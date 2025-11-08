package com.dasi.web.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.exception.UniqueFieldConflictException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
@Slf4j
public class UniqueFieldAspect {

    @Autowired
    private ApplicationContext context;

    @Before("@annotation(uniqueField)")
    public void before(JoinPoint joinPoint, UniqueField uniqueField) {
        // 获取 dto
        Object dto = joinPoint.getArgs()[0];

        // 获取 Service
        @SuppressWarnings("unchecked")
        IService<Object> service = (IService<Object>) context.getBean(uniqueField.serviceClass());

        // 获取值
        Object fieldValue = tryGetFieldValue(dto, uniqueField.fieldName());
        Object idValue = tryGetFieldValue(dto, uniqueField.idName());

        // 查询
        if (service.count(new QueryWrapper<>()
                .eq(fieldValue != null, uniqueField.fieldName(), fieldValue)
                .ne(idValue != null, uniqueField.idName(), idValue)) > 0) {
            throw new UniqueFieldConflictException(uniqueField.resultInfo());
        }
    }

    private Object tryGetFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}

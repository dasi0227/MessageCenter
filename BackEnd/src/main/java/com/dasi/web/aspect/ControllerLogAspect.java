package com.dasi.web.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Autowired
    private ObjectMapper mapper;

    @Pointcut("execution(* com.dasi.core.controller..*(..))") // 改成你自己的包路径
    public void controllerLog() {}

    @Before("controllerLog()")
    public void logRequest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
               log.debug("【请求参数】{}", mapper.writeValueAsString(arg));
            } catch (Exception ignore) {}
        }
    }

    @AfterReturning(pointcut = "controllerLog()", returning = "result")
    public void logResponse(Object result) {
        try {
            String json = mapper.writeValueAsString(result);
            if (json.length() > 100) {
                json = json.substring(0, 100) + "...(省略)";
            }
            log.debug("【返回结果】{}", json);
        } catch (Exception ignore) {}
    }
}

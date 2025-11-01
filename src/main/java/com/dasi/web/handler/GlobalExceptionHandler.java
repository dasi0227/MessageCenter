package com.dasi.web.handler;

import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result<Void> exceptionHandler(MessageCenterException ex) {
        ResultInfo resultInfo = ex.getResultInfo();
        log.error("返回错误信息：Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage());
        return Result.fail(resultInfo);
    }
}

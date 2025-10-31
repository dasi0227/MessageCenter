package com.dasi.controller;

import com.dasi.pojo.enumeration.ResultInfo;
import com.dasi.pojo.exception.MessageCenterException;
import com.dasi.pojo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result<Void> exceptionHandler(MessageCenterException ex) {
        ResultInfo resultInfo = ex.getResultInfo();
        log.error("遇到错误：状态吗={}, 错误信息={}", resultInfo.getCode(), resultInfo.getMessage());
        return Result.fail(resultInfo);
    }
}

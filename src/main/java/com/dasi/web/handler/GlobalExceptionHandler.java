package com.dasi.web.handler;

import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result<Void> exceptionHandler(MessageCenterException exception) {
        ResultInfo resultInfo = exception.getResultInfo();
        log.error("【消息中心错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage());
        return Result.fail(resultInfo);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(MethodArgumentNotValidException exception) {
        ResultInfo resultInfo = ResultInfo.PARAM_INFO_ERROR;
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("【参数校验错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage() + ":" + message);
        return Result.fail(resultInfo, message);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(DataAccessException exception) {
        ResultInfo resultInfo = ResultInfo.DATABASE_ERROR;
        String message = exception.getMessage();
        log.error("【数据库错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage() + ":" + message);
        return Result.fail(resultInfo, message);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(Exception exception) {
        ResultInfo resultInfo = ResultInfo.SERVER_ERROR;
        String message = exception.getMessage();
        log.error("【服务器错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage() + ":" + message);
        return Result.fail(resultInfo, message);
    }
}

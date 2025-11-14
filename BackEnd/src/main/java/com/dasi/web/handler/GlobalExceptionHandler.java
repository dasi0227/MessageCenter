package com.dasi.web.handler;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.common.exception.RateLimitException;
import com.dasi.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result<Void> exceptionHandler(MessageCenterException exception) {
        ResultInfo resultInfo = exception.getResultInfo();
        log.error("【消息中心业务错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage());
        exception.printStackTrace(System.err);
        return Result.fail(resultInfo);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(MethodArgumentNotValidException exception) {
        ResultInfo resultInfo = ResultInfo.PARAM_ERROR;
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("【参数校验错误】Code={}, Message={}", resultInfo.getCode(), resultInfo.getMessage() + ":" + message);
        exception.printStackTrace(System.err);
        return solveException(ResultInfo.PARAM_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(RateLimitException exception) {
        return solveException(ResultInfo.RATE_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> handleRedisSystemException(RedisSystemException exception) {
        return solveException(ResultInfo.REDIS_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(DataAccessException exception) {
        return solveException(ResultInfo.MYSQL_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(AmqpException exception) {
        return solveException(ResultInfo.MQ_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(OSSException exception) {
        return solveException(ResultInfo.OSS_ERROR, exception);
    }

    @ExceptionHandler
    public Result<Void> exceptionHandler(ClientException exception) {
        return solveException(ResultInfo.OSS_ERROR, exception);
    }


    @ExceptionHandler
    public Result<Void> exceptionHandler(Exception exception) {
        return solveException(ResultInfo.JVM_ERROR, exception);
    }

    private Result<Void> solveException(ResultInfo info, Exception exception) {
        String message = exception.getMessage();
        log.error("【{}】Code={}, Message={}", info.getMessage(), info.getCode(), message);
        exception.printStackTrace(System.err);
        return Result.fail(info, message);
    }
}

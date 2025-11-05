package com.dasi.common.result;

import com.dasi.common.enumeration.ResultInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;       // 状态码
    private String msg;     // 提示信息
    private T data;             // 数据

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultInfo.SUCCESS.getCode());
        result.setMsg(ResultInfo.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(ResultInfo resultInfo) {
        Result<T> result = new Result<>();
        result.setCode(resultInfo.getCode());
        result.setMsg(resultInfo.getMessage());
        return result;
    }

    public static <T> Result<T> fail(ResultInfo resultInfo, String message) {
        Result<T> result = new Result<>();
        result.setCode(resultInfo.getCode());
        result.setMsg((resultInfo.getMessage() + ": " + message));
        return result;
    }
}

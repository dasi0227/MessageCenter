package com.dasi.pojo.exception;

import com.dasi.pojo.enumeration.ResultInfo;
import lombok.Getter;

@Getter
public class MessageCenterException extends RuntimeException {

    private final ResultInfo resultInfo;

    public MessageCenterException(ResultInfo resultInfo) {
        super(resultInfo.getMessage());
        this.resultInfo = resultInfo;
    }
}

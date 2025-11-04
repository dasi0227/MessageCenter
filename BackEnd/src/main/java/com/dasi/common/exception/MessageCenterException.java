package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;
import lombok.Getter;

@Getter
public class MessageCenterException extends RuntimeException {

    private final ResultInfo resultInfo;

    public MessageCenterException(ResultInfo resultInfo) {
        super(resultInfo.getMessage());
        this.resultInfo = resultInfo;
    }
}

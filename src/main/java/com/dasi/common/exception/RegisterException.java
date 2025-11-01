package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class RegisterException extends MessageCenterException {
    public RegisterException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

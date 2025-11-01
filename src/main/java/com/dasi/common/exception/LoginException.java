package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class LoginException extends MessageCenterException {
    public LoginException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class UserException extends MessageCenterException {
    public UserException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}
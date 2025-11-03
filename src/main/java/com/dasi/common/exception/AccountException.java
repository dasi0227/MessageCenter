package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class AccountException extends MessageCenterException {
    public AccountException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}
package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class ContactException extends MessageCenterException {
    public ContactException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

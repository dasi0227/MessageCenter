package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class SensitiveWordException extends MessageCenterException {
    public SensitiveWordException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

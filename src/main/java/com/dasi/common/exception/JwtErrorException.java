package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class JwtErrorException extends MessageCenterException {
    public JwtErrorException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}
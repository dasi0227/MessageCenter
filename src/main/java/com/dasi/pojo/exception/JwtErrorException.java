package com.dasi.pojo.exception;

import com.dasi.pojo.enumeration.ResultInfo;

public class JwtErrorException extends MessageCenterException {
    public JwtErrorException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}
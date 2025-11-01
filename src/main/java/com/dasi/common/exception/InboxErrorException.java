package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class InboxErrorException extends MessageCenterException {
    public InboxErrorException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

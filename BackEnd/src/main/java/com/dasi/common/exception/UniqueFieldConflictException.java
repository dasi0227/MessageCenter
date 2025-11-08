package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class UniqueFieldConflictException extends MessageCenterException {
    public UniqueFieldConflictException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

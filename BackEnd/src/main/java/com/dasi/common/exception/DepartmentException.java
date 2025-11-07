package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class DepartmentException extends MessageCenterException {
    public DepartmentException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

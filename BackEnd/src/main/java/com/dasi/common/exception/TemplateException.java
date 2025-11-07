package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class TemplateException extends MessageCenterException {
    public TemplateException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

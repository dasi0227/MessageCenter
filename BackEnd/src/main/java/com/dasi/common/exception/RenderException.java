package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class RenderException extends MessageCenterException {
    public RenderException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

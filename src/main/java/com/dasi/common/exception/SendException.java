package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class SendException extends MessageCenterException {
    public SendException(ResultInfo resultInfo) {
      super(resultInfo);
    }
}

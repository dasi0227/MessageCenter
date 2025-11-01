package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class PermissionException extends MessageCenterException {
    public PermissionException(ResultInfo resultInfo) {
      super(resultInfo);
    }
}

package com.dasi.common.exception;

import com.dasi.common.enumeration.ResultInfo;

public class MailboxException extends MessageCenterException {
    public MailboxException(ResultInfo resultInfo) {
        super(resultInfo);
    }
}

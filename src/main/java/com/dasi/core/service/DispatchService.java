package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;

public interface DispatchService extends IService<Dispatch> {
    Mailbox selectMailboxInfo(Long dispatchId);

    void updateFinishStatus(Long id, MsgStatus status, String errorMsg);

    void updateSendStatus(Long dispatchId);
}

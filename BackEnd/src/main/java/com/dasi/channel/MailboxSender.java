package com.dasi.channel;

import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.SendException;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Mailbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class MailboxSender {

    @Autowired
    private MailboxMapper mailboxMapper;

    @Autowired
    private DispatchService dispatchService;

    @Transactional
    public void send(Long dispatchId) {
        try {
            Mailbox mailbox = dispatchService.selectMailboxInfo(dispatchId);
            mailboxMapper.insert(mailbox);
            dispatchService.updateFinishStatus(dispatchId, MsgStatus.SUCCESS, null);
            log.debug("【投递器】投递站内信成功：{}", mailbox);
        } catch (Exception e) {
            log.error("【投递器】投递站内信失败：{}", e.getMessage());
            dispatchService.updateFinishStatus(dispatchId, MsgStatus.FAIL, e.getMessage());
            throw new SendException(ResultInfo.SEND_MAILBOX_FAIL);
        }
    }
}

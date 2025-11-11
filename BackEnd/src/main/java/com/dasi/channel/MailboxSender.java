package com.dasi.channel;

import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.core.service.DispatchService;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MailboxSender {

    @Autowired
    private MailboxService mailboxService;

    @Autowired
    private DispatchService dispatchService;

    @Transactional
    public void send(Dispatch dispatch) {
        try {
            Mailbox mailbox = Mailbox.builder()
                    .inbox(Long.valueOf(dispatch.getTarget()))
                    .departmentName(dispatch.getDepartmentName())
                    .subject(dispatch.getSubject())
                    .content(dispatch.getContent())
                    .attachments(dispatch.getAttachments())
                    .isRead(0)
                    .isDeleted(0)
                    .arrivedAt(LocalDateTime.now())
                    .build();
            mailboxService.save(mailbox);
            dispatchService.updateFinishStatus(dispatch, MsgStatus.SUCCESS, null);
            log.debug("【MailboxSender】站内信投递成功：{}", mailbox);
        } catch (Exception e) {
            String errorMsg = SendConstant.SEND_MAILBOX_FAIL + e.getMessage();
            dispatchService.updateFinishStatus(dispatch, MsgStatus.FAIL, errorMsg);
            log.error("【MailboxSender】站内信投递失败：{}", errorMsg);
            throw new SendException(errorMsg);
        }
    }
}

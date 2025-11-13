package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.ContactUpdate4MailboxDTO;
import com.dasi.pojo.dto.ContactLoginDTO;
import com.dasi.pojo.dto.MailboxPageDTO;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.MailboxLoginVO;
import jakarta.validation.Valid;

public interface MailboxService extends IService<Mailbox> {
    void readMailbox(Long id, Integer isRead);

    void deleteMailbox(Long id, Integer isDeleted);

    void removeMailbox(Long id);

    PageResult<Mailbox> getMailboxPage(@Valid MailboxPageDTO dto);

    MailboxLoginVO login(@Valid ContactLoginDTO dto);

    void updateContact(@Valid ContactUpdate4MailboxDTO dto);
}

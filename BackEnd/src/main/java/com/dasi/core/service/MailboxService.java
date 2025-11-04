package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.entity.Mailbox;

public interface MailboxService extends IService<Mailbox> {
    void readMailbox(Long id, Integer isRead);

    void deleteMailbox(Long id, Integer isDeleted);

    void removeMailbox(Long id);
}

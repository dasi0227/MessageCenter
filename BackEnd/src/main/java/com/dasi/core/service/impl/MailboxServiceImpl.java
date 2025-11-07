package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MailboxException;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.entity.Mailbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MailboxServiceImpl extends ServiceImpl<MailboxMapper, Mailbox> implements MailboxService {

    @Autowired
    private MailboxMapper mailboxMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMailbox(Long id, Integer isRead) {
        if (!update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsRead, isRead)
                .set(isRead == 1, Mailbox::getReadAt, LocalDateTime.now())
                .set(isRead == 0, Mailbox::getReadAt, null))) {
            throw new MailboxException(ResultInfo.MAILBOX_UPDATE_FAIL);
        }

        log.debug("【Mailbox Service】更新站内信已读状态：id={}, isRead={}", id, isRead);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMailbox(Long id, Integer isDeleted) {
        if (!update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsDeleted, isDeleted))) {
            throw new MailboxException(ResultInfo.MAILBOX_UPDATE_FAIL);
        }

        log.debug("【Mailbox Service】更新站内信删除状态：id={}, isDeleted={}", id, isDeleted);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMailbox(Long id) {
        if (!removeById(id)) {
            throw new MailboxException(ResultInfo.MAILBOX_REMOVE_FAIL);
        }

        log.debug("【Mailbox Service】销毁站内信：id={}", id);
    }
}

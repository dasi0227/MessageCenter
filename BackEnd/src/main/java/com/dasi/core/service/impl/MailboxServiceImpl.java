package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.entity.Mailbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MailboxServiceImpl extends ServiceImpl<MailboxMapper, Mailbox> implements MailboxService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMailbox(Long id, Integer isRead) {
        boolean flag = update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsRead, isRead)
                .set(isRead == 1, Mailbox::getReadAt, LocalDateTime.now())
                .set(isRead == 0, Mailbox::getReadAt, null));

        if (!flag) {
            log.warn("【Mailbox Service】更新已读状态失败：id={}, isRead={}", id, isRead);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMailbox(Long id, Integer isDeleted) {
        boolean flag = update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsDeleted, isDeleted));

        if (!flag) {
            log.warn("【Mailbox Service】更新删除状态失败：id={}, isDeleted={}", id, isDeleted);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMailbox(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Mailbox Service】销毁失败，站内信不存在：id={}", id);
        }
    }
}

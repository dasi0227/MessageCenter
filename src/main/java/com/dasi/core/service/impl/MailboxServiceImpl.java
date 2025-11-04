package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.entity.Mailbox;
import org.springframework.stereotype.Service;

@Service
public class MailboxServiceImpl extends ServiceImpl<MailboxMapper, Mailbox> implements MailboxService {
}

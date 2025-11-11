package com.dasi;

import com.dasi.channel.EmailSender;
import com.dasi.common.context.AccountContext;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.pojo.entity.Dispatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest
public class MailSenderTest {

    @Autowired
    private EmailSender emailSender;

    @Test
    public void test() {
        AccountContextHolder.set(new AccountContext(1L, AccountRole.ADMIN, "dasi"));

        Dispatch dispatch = Dispatch.builder()
                .id(0L)
                .messageId(1L)
                .subject("测试标题")
                .content("测试内容")
                .attachments(Collections.emptyList())
                .departmentId(0L)
                .departmentName("测试部门")
                .contactId(0L)
                .contactName("测试收件人")
                .target("wanyw0227@gmail.com")
                .status(MsgStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        emailSender.send(dispatch);
    }
}

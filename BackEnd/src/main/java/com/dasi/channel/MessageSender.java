package com.dasi.channel;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.FailureStatus;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.core.service.DepartmentService;
import com.dasi.core.service.DispatchService;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.entity.Department;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Failure;
import com.dasi.pojo.entity.Mailbox;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class MessageSender {

    @Autowired
    @Qualifier("departmentMailMap")
    private Map<String, JavaMailSender> departmentMailMap;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private MailboxService mailboxService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    public void sendEmail(Dispatch dispatch) {
        try {
            Department department = departmentService.getById(dispatch.getDepartmentId());
            String email = department.getEmail();
            String departmentName = department.getName();

            JavaMailSender mailSender = departmentMailMap.get(email);
            if (mailSender == null) {
                throw new SendException(SendConstant.MAIL_UNAVAILABLE + departmentName);
            }

            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(email, SystemConstant.SERVER_NAME + departmentName));
            helper.setTo(dispatch.getTarget());
            helper.setSubject(dispatch.getSubject());
            helper.setText(dispatch.getContent(), false);
            if (dispatch.getAttachments() != null) {
                for (String url : dispatch.getAttachments()) {
                    File file = new File(url);
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(mail);
            dispatchService.updateFinishStatus(dispatch, MsgStatus.SUCCESS, null);
            log.debug("【EmailSender】邮件投递成功：{}", dispatch.getTarget());
        } catch (Exception exception) {
            String errorMsg = SendConstant.SEND_EMAIL_FAIL + exception.getMessage();
            dispatchService.updateFinishStatus(dispatch, MsgStatus.ERROR, errorMsg);
            log.error("【EmailSender】邮件投递失败：{}", errorMsg);
            sendDlx(dispatch, exception);
        }
    }

    public void sendMailbox(Dispatch dispatch) {
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
        } catch (Exception exception) {
            String errorMsg = SendConstant.SEND_MAILBOX_FAIL + exception.getMessage();
            dispatchService.updateFinishStatus(dispatch, MsgStatus.ERROR, errorMsg);
            log.error("【MailboxSender】站内信投递失败：{}", errorMsg);
            sendDlx(dispatch, exception);
        }
    }

    public void sendDlx(Dispatch dispatch, Exception exception) {
        Failure failure = Failure.builder()
                .dispatchId(String.valueOf(dispatch.getId()))
                .errorType(exception.getClass().getName())
                .errorMessage(exception.getMessage())
                .errorStack(ExceptionUtil.stacktraceToString(exception))
                .status(FailureStatus.UNHANDLED)
                .createdAt(LocalDateTime.now())
                .payload(dispatch)
                .build();
        log.debug("【DlxSender】消息发送失败，发送到死信队列：{}", failure);
        rabbitTemplate.convertAndSend(rabbitMqProperties.getDlxExchange(), rabbitMqProperties.getDlxRoute(), failure);
    }

    public void sendSms(Dispatch dispatch) {
    }
}

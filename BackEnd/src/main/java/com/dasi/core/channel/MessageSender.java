package com.dasi.core.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.core.service.DepartmentService;
import com.dasi.core.service.DispatchService;
import com.dasi.core.service.MailboxService;
import com.dasi.core.service.OssFileService;
import com.dasi.pojo.entity.Department;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.entity.OssFile;
import com.dasi.util.AliOssUtil;
import com.dasi.util.WeComUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MessageSender {

    @Autowired
    @Qualifier("mailMap")
    private Map<String, JavaMailSender> mailMap;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private MailboxService mailboxService;

    @Autowired
    private DlxSender dlxSender;

    @Autowired
    private WeComUtil weComUtil;

    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private AliOssUtil aliOssUtil;

    @CacheEvict(value = RedisConstant.CACHE_MAILBOX_PREFIX, allEntries = true)
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
            dispatchService.updateStatus(dispatch, MsgStatus.SUCCESS, null);
            log.debug("【MailboxSender】站内信投递成功：{}", mailbox);
        } catch (Exception exception) {
            String errorMsg = SendConstant.SEND_MAILBOX_FAIL + exception.getMessage();
            dispatchService.updateStatus(dispatch, MsgStatus.ERROR, errorMsg);
            dlxSender.sendDlx(dispatch, exception);
        }
    }

    public void sendEmail(Dispatch dispatch) {
        try {
            Department department = departmentService.getById(dispatch.getDepartmentId());
            String email = department.getEmail();
            String departmentName = department.getName();

            JavaMailSender mailSender = mailMap.get(email);
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
            dispatchService.updateStatus(dispatch, MsgStatus.SUCCESS, null);
            log.debug("【EmailSender】邮件投递成功：{}", dispatch.getTarget());
        } catch (Exception exception) {
            String errorMsg = SendConstant.SEND_EMAIL_FAIL + exception.getMessage();
            dispatchService.updateStatus(dispatch, MsgStatus.ERROR, errorMsg);
            dlxSender.sendDlx(dispatch, exception);
        }
    }

    public void sendWeCom(Dispatch dispatch) {
        try {
            // 1. 构造文本
            String text = "%s\n来自：%s\n标题：%s\n\n%s".formatted(
                    SystemConstant.SERVER_NAME,
                    dispatch.getDepartmentName(),
                    dispatch.getSubject(),
                    dispatch.getContent()
            );

            // 2. 解析 userid
            String userId = weComUtil.getUserIdByPhone(dispatch.getTarget());

            // 3. 发送文本
            weComUtil.sendText(userId, text);

            // 4. 获取 url 对应的 objectName
            List<OssFile> files = ossFileService.list(new LambdaQueryWrapper<OssFile>()
                    .in(OssFile::getUrl, dispatch.getAttachments())
            );

            // 5. 发送文件
            files.forEach(file -> {
                String objectName = file.getObjectName();
                byte[] bytes = aliOssUtil.getObject(objectName);
                String mediaId = weComUtil.uploadFile(bytes, file.getFileName());
                weComUtil.sendFile(userId, mediaId);
            });

            dispatchService.updateStatus(dispatch, MsgStatus.SUCCESS, null);
            log.debug("【WeComSender】企业微信投递成功：phone={}, userId={}", dispatch.getTarget(), userId);
        } catch (Exception exception) {
            String errorMsg = SendConstant.SEND_WECOM_FAIL + exception.getMessage();
            dispatchService.updateStatus(dispatch, MsgStatus.ERROR, errorMsg);
            dlxSender.sendDlx(dispatch, exception);
        }
    }
}

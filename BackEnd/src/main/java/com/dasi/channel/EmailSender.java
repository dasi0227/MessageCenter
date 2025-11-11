package com.dasi.channel;

import com.dasi.common.constant.SystemConstant;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.exception.SendException;
import com.dasi.core.service.DepartmentService;
import com.dasi.core.service.DispatchService;
import com.dasi.pojo.entity.Department;
import com.dasi.pojo.entity.Dispatch;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class EmailSender {

    @Autowired
    @Qualifier("departmentMailMap")
    private Map<String, JavaMailSender> departmentMailMap;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DispatchService dispatchService;

    public void send(Dispatch dispatch) {
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
            log.info("【EmailSender】邮件投递成功：{}", dispatch.getTarget());
        } catch (Exception e) {
            String errorMsg = SendConstant.SEND_EMAIL_FAIL + e.getMessage();
            dispatchService.updateFinishStatus(dispatch, MsgStatus.FAIL, errorMsg);
            log.error("【EmailSender】邮件投递失败：{}", errorMsg);
            throw new SendException(errorMsg);
        }
    }
}

package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.dto.ContactUpdate4MailboxDTO;
import com.dasi.pojo.dto.ContactLoginDTO;
import com.dasi.pojo.dto.MailboxPageDTO;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.MailboxLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    @Autowired
    private MailboxService mailboxService;

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String newToken = mailboxService.refresh(request);
        return Result.success(newToken);
    }

    @PostMapping("/page")
    public Result<PageResult<Mailbox>> getMailboxPage(@Valid @RequestBody MailboxPageDTO dto) {
        PageResult<Mailbox> result = mailboxService.getMailboxPage(dto);
        return Result.success(result);
    }

    @PostMapping("/read/{id}")
    public Result<Void> readMailbox(@PathVariable("id") Long id, @RequestParam("isRead") Integer isRead) {
        mailboxService.readMailbox(id, isRead);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    public Result<Void> deleteMailbox(@PathVariable("id") Long id, @RequestParam("isDeleted") Integer isDeleted) {
        mailboxService.deleteMailbox(id, isDeleted);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeMailbox(@PathVariable("id") Long id) {
        mailboxService.removeMailbox(id);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<MailboxLoginVO> login(@Valid @RequestBody ContactLoginDTO dto) {
        MailboxLoginVO vo = mailboxService.login(dto);
        return Result.success(vo);
    }

    @PostMapping("/update")
    public Result<Void> updateContact(@Valid @RequestBody ContactUpdate4MailboxDTO dto) {
        mailboxService.updateContact(dto);
        return Result.success();
    }

}
package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.MailboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    @Autowired
    private MailboxService mailboxService;

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

}
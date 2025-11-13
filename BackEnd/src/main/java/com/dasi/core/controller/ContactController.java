package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.ContactService;
import com.dasi.pojo.dto.*;
import com.dasi.pojo.entity.Contact;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/page")
    public Result<PageResult<Contact>> getContactPage(@Valid @RequestBody ContactPageDTO dto) {
        PageResult<Contact> pageResult = contactService.getContactPage(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Contact>> getContactList() {
        List<Contact> result = contactService.getContactList();
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<Void> addContact(@Valid @RequestBody ContactAddDTO dto) {
        contactService.addContact(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateContact(@Valid @RequestBody ContactUpdateDTO dto) {
        contactService.updateContact(dto);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeContact(@PathVariable("id") Long id) {
        contactService.removeContact(id);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody ContactStatusDTO dto) {
        contactService.updateStatus(dto);
        return Result.success();
    }

}

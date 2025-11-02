package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.ContactService;
import com.dasi.pojo.dto.ContactDTO;
import com.dasi.pojo.dto.ContactPageDTO;
import com.dasi.pojo.dto.StatusDTO;
import com.dasi.pojo.entity.Contact;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/page")
    public Result<PageResult<Contact>> getContacts(@Valid @RequestBody ContactPageDTO contactPageDTO) {
        PageResult<Contact> pageResult = contactService.getContacts(contactPageDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/add")
    public Result<Void> addContact(@Valid @RequestBody ContactDTO contactDTO) {
        contactService.addContact(contactDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateContact(@Valid @RequestBody ContactDTO contactDTO) {
        contactService.updateContact(contactDTO);
        return Result.success();
    }

    @DeleteMapping("/remove/{id}")
    public Result<Void> removeContact(@PathVariable("id") Long id) {
        contactService.removeContact(id);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO statusDTO) {
        contactService.updateStatus(statusDTO);
        return Result.success();
    }
}

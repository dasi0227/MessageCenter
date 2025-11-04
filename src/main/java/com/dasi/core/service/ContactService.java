package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.*;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.ContactLoginVO;
import jakarta.validation.Valid;

public interface ContactService extends IService<Contact> {
    void addContact(ContactAddDTO dto);

    void removeContact(Long id);

    void updateContact(ContactUpdateDTO dto);

    PageResult<Contact> getContactPage(ContactPageDTO dto);

    void updateStatus(ContactStatusDTO dto);

    String resolveTarget(Long contactId, MsgChannel channel);

    ContactLoginVO login(@Valid ContactLoginDTO dto);

    PageResult<Mailbox> getMailboxPage(@Valid MailboxPageDTO dto);
}

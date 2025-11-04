package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.ContactAddDTO;
import com.dasi.pojo.dto.ContactPageDTO;
import com.dasi.pojo.dto.ContactStatusDTO;
import com.dasi.pojo.dto.ContactUpdateDTO;
import com.dasi.pojo.entity.Contact;
import jakarta.validation.Valid;

public interface ContactService extends IService<Contact> {
    void addContact(ContactAddDTO dto);

    void removeContact(Long id);

    void updateContact(ContactUpdateDTO dto);

    PageResult<Contact> getContactPage(ContactPageDTO dto);

    void updateStatus(ContactStatusDTO dto);

    String resolveTarget(Long contactId, MsgChannel channel);
}

package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.ContactDTO;
import com.dasi.pojo.dto.ContactPageDTO;
import com.dasi.pojo.dto.StatusDTO;
import com.dasi.pojo.entity.Contact;

public interface ContactService extends IService<Contact> {
    void addContact(ContactDTO contactDTO);

    void removeContact(Long id);

    void updateContact(ContactDTO contactDTO);

    void updateStatus(StatusDTO statusDTO);

    PageResult<Contact> getContacts(ContactPageDTO contactPageDTO);
}

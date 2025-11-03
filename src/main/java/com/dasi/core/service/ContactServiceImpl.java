package com.dasi.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.ContactException;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.pojo.dto.ContactAddDTO;
import com.dasi.pojo.dto.ContactPageDTO;
import com.dasi.pojo.dto.ContactStatusDTO;
import com.dasi.pojo.dto.ContactUpdateDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.util.InboxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {
    @Autowired
    private InboxUtil inboxUtil;

    @Override
    @AutoFill(FillType.INSERT)
    public void addContact(ContactAddDTO dto) {
        // 检查重名
        if (exists(new LambdaQueryWrapper<Contact>().eq(Contact::getName, dto.getName()))) {
            throw new ContactException(ResultInfo.CONTACT_ALREADY_EXIST);
        }

        // 构建联系人
        Contact contact = BeanUtil.copyProperties(dto, Contact.class);
        contact.setPassword(SecureUtil.md5(dto.getPassword()));
        contact.setInbox(inboxUtil.nextId());
        save(contact);

        log.debug("【Contact Service】新增联系人：{}", dto);
    }

    @Override
    public void removeContact(Long id) {
        if (!removeById(id)) {
            throw new ContactException(ResultInfo.CONTACT_REMOVE_ERROR);
        }
        log.debug("【Contact Service】删除联系人：{}", id);
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    public void updateContact(ContactUpdateDTO dto) {
        Contact contact = getById(dto.getId());
        if (contact == null) {
            throw new ContactException(ResultInfo.CONTACT_NOT_EXIST);
        }

        BeanUtils.copyProperties(dto, contact);
        if (!updateById(contact)) {
            throw new ContactException(ResultInfo.CONTACT_UPDATE_ERROR);
        }

        log.debug("【Contact Service】更新联系人：{}", dto);
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    public void updateStatus(ContactStatusDTO dto) {
        if (!update(new LambdaUpdateWrapper<Contact>()
                .eq(Contact::getId, dto.getId())
                .set(Contact::getStatus, dto.getStatus())
                .set(Contact::getUpdatedAt, dto.getUpdatedAt()))) {
            throw new ContactException(ResultInfo.CONTACT_UPDATE_ERROR);
        }
        log.debug("【Contact Service】更新联系人状态：{}", dto);
    }

    @Override
    public PageResult<Contact> getContactPage(ContactPageDTO dto) {
        Page<Contact> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<Contact>()
                .like(StrUtil.isNotBlank(dto.getName()), Contact::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getPhone()), Contact::getPhone, dto.getPhone())
                .like(StrUtil.isNotBlank(dto.getEmail()), Contact::getEmail, dto.getEmail())
                .eq(dto.getStatus() != null, Contact::getStatus, dto.getStatus())
                .orderByDesc(Contact::getName);

        Page<Contact> contacts = page(param, wrapper);

        log.debug("【Contact Service】分页查询联系人：{}", dto);
        return PageResult.of(contacts);
    }

}

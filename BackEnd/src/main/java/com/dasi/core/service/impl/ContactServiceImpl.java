package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.SendException;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.service.ContactService;
import com.dasi.pojo.dto.*;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.util.InboxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    @Autowired
    private InboxUtil inboxUtil;

    @Override
    @Cacheable(value = RedisConstant.CACHE_CONTACT_PREFIX, key = "'page:' + #dto.hashCode()")
    public PageResult<Contact> getContactPage(ContactPageDTO dto) {
        Page<Contact> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<Contact>()
                .like(StrUtil.isNotBlank(dto.getName()), Contact::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getPhone()), Contact::getPhone, dto.getPhone())
                .like(StrUtil.isNotBlank(dto.getEmail()), Contact::getEmail, dto.getEmail())
                .eq(dto.getStatus() != null, Contact::getStatus, dto.getStatus())
                .orderByAsc(Contact::getName);

        Page<Contact> contacts = page(param, wrapper);

        return PageResult.of(contacts);
    }

    @Override
    @Cacheable(value = RedisConstant.CACHE_CONTACT_PREFIX, key = "'list'")
    public List<Contact> getContactList() {
        return list();
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @UniqueField(fieldName = "name", resultInfo = ResultInfo.CONTACT_NAME_ALREADY_EXISTS)
    @CacheEvict(value = RedisConstant.CACHE_CONTACT_PREFIX, allEntries = true)
    public void addContact(ContactAddDTO dto) {
        Contact contact = BeanUtil.copyProperties(dto, Contact.class);
        contact.setPassword(SecureUtil.md5(dto.getPassword()));
        contact.setInbox(inboxUtil.nextId());
        boolean flag = save(contact);

        if (!flag) {
            log.warn("【Contact Service】新增联系人失败：{}", dto);
        }
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @UniqueField(fieldName = "name", resultInfo = ResultInfo.CONTACT_NAME_ALREADY_EXISTS)
    @CacheEvict(value = RedisConstant.CACHE_CONTACT_PREFIX, allEntries = true)
    public void updateContact(ContactUpdateDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<Contact>()
                .eq(Contact::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Contact::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getPhone()), Contact::getPhone, dto.getPhone())
                .set(StrUtil.isNotBlank(dto.getEmail()), Contact::getEmail, dto.getEmail())
                .set(StrUtil.isNotBlank(dto.getPassword()), Contact::getPassword, SecureUtil.md5(dto.getPassword()))
                .set(Contact::getStatus, dto.getStatus())
                .set(Contact::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Contact Service】更新失败，没有记录或值无变化：{}", dto);
        }
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisConstant.CACHE_CONTACT_PREFIX, allEntries = true)
    public void updateStatus(ContactStatusDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<Contact>()
                .eq(Contact::getId, dto.getId())
                .set(Contact::getStatus, dto.getStatus())
                .set(Contact::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Contact Service】更新联系人状态失败：{}", dto);
        }
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisConstant.CACHE_CONTACT_PREFIX, allEntries = true)
    public void removeContact(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Contact Service】删除失败，联系人不存在：{}", id);
        }
    }

    @Override
    public String resolveTarget(Long contactId, MsgChannel channel) {
        Contact contact = getById(contactId);
        if (contact == null) {
            throw new SendException(SendConstant.CONTACT_NOT_FOUND + contactId);
        }

        String target = switch (channel) {
            case EMAIL -> contact.getEmail();
            case SMS -> contact.getPhone();
            case MAILBOX -> String.valueOf(contact.getInbox());
        };

        if (target == null || target.trim().isEmpty()) {
            throw new SendException(SendConstant.CONTACT_TARGET_NOT_FOUND + channel);
        }

        return target;
    }

    @Override
    public void check(Dispatch dispatch) {
        Contact contact = getById(dispatch.getContactId());
        if (contact == null) {
            String errorMsg = SendConstant.CONTACT_MISSING + dispatch.getContactName();
            log.warn("【联系人检查】{}", errorMsg);
            throw new SendException(errorMsg);
        }
        if (contact.getStatus() == null || contact.getStatus() == 0) {
            String errorMsg = SendConstant.CONTACT_DISABLED + dispatch.getContactName();
            log.warn("【联系人检查】{}", errorMsg);
            throw new SendException(errorMsg);
        }
    }

}

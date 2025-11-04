package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.context.ContactContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.AccountException;
import com.dasi.common.exception.ContactException;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.ContactService;
import com.dasi.pojo.dto.*;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.ContactLoginVO;
import com.dasi.util.InboxUtil;
import com.dasi.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    @Autowired
    private InboxUtil inboxUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailboxMapper mailboxMapper;

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
    public String resolveTarget(Long contactId, MsgChannel channel) {
        Contact contact = getById(contactId);
        if (contact == null) {
            throw new ContactException(ResultInfo.CONTACT_NOT_EXIST);
        }

        return switch (channel) {
            case EMAIL -> contact.getEmail();
            case SMS -> contact.getPhone();
            case MAILBOX -> String.valueOf(contact.getInbox());
        };
    }

    @Override
    public ContactLoginVO login(ContactLoginDTO dto) {
        // 查询联系人
        Contact contact = getOne(new LambdaQueryWrapper<Contact>().eq(Contact::getName, dto.getName()), false);
        if (contact == null) {
            throw new AccountException(ResultInfo.CONTACT_NOT_EXIST);
        }

        // 校验密码
        String password = SecureUtil.md5(dto.getPassword());
        log.debug("dto password：{}", password);
        log.debug("account password：{}", contact.getPassword());
        if (!password.equals(contact.getPassword())) {
            throw new AccountException(ResultInfo.CONTACT_PASSWORD_ERROR);
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getContactIdClaimKey(), contact.getId());
        claims.put(jwtProperties.getContactInboxClaimKey(), contact.getInbox());
        String token = jwtUtil.createToken(claims);

        // 构造视图
        ContactLoginVO vo = BeanUtil.copyProperties(contact, ContactLoginVO.class);
        vo.setToken(token);

        log.debug("【Contact Service】联系人登陆：{}", dto);

        return vo;
    }

    @Override
    public PageResult<Mailbox> getMailboxPage(MailboxPageDTO dto) {
        Long contactId = ContactContextHolder.get().getId();
        Long inbox = ContactContextHolder.get().getInbox();

        Page<Mailbox> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Mailbox> wrapper = new LambdaQueryWrapper<Mailbox>()
                .eq(Mailbox::getInbox, inbox)
                .eq(dto.getIs_read() != null, Mailbox::getIsRead, dto.getIs_read())
                .eq(dto.getIs_deleted() != null, Mailbox::getIsDeleted, dto.getIs_deleted())
                .like(StrUtil.isNotBlank(dto.getAddresser()), Mailbox::getAddresser, dto.getAddresser())
                .like(StrUtil.isNotBlank(dto.getSubject()), Mailbox::getSubject, dto.getSubject())
                .like(StrUtil.isNotBlank(dto.getContent()), Mailbox::getContent, dto.getContent())
                .orderByDesc(Mailbox::getArrivedAt);
        IPage<Mailbox> result = mailboxMapper.selectPage(page, wrapper);

        log.debug("【Mailbox Service】查询联系人信箱：contactId={}, inbox={}", contactId, inbox);
        return PageResult.of(result);
    }

    @Override
    public PageResult<Contact> getContactPage(ContactPageDTO dto) {
        Page<Contact> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<Contact>()
                .like(StrUtil.isNotBlank(dto.getName()), Contact::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getPhone()), Contact::getPhone, dto.getPhone())
                .like(StrUtil.isNotBlank(dto.getEmail()), Contact::getEmail, dto.getEmail())
                .eq(dto.getStatus() != null, Contact::getStatus, dto.getStatus())
                .orderByAsc(Contact::getName);

        Page<Contact> contacts = page(param, wrapper);

        log.debug("【Contact Service】分页查询联系人：{}", dto);
        return PageResult.of(contacts);
    }

}

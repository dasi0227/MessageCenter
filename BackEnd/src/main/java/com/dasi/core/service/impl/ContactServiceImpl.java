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
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.context.ContactContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.AccountException;
import com.dasi.common.exception.ContactException;
import com.dasi.common.exception.SendException;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.ContactService;
import com.dasi.pojo.dto.*;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.ContactLoginVO;
import com.dasi.util.InboxUtil;
import com.dasi.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
    public List<Contact> getContactList() {
        return list();
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @UniqueField(fieldName = "name", resultInfo = ResultInfo.CONTACT_NAME_ALREADY_EXISTS)
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
    public void removeContact(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Contact Service】删除失败，联系人不存在：{}", id);
        }
    }

    @Override
    public ContactLoginVO login(ContactLoginDTO dto) {
        // 查询联系人
        Contact contact = getOne(new LambdaQueryWrapper<Contact>().eq(Contact::getName, dto.getName()), false);
        if (contact == null) {
            throw new AccountException(ResultInfo.CONTACT_NOT_FOUND);
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
        Long inbox = ContactContextHolder.get().getInbox();
        Page<Mailbox> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Mailbox> wrapper = new LambdaQueryWrapper<Mailbox>()
                .eq(Mailbox::getInbox, inbox)
                .eq(dto.getIs_read() != null, Mailbox::getIsRead, dto.getIs_read())
                .eq(dto.getIs_deleted() != null, Mailbox::getIsDeleted, dto.getIs_deleted())
                .like(StrUtil.isNotBlank(dto.getDepartmentName()), Mailbox::getDepartmentName, dto.getDepartmentName())
                .like(StrUtil.isNotBlank(dto.getSubject()), Mailbox::getSubject, dto.getSubject())
                .like(StrUtil.isNotBlank(dto.getContent()), Mailbox::getContent, dto.getContent())
                .orderByDesc(Mailbox::getArrivedAt);

        IPage<Mailbox> result = mailboxMapper.selectPage(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public String resolveTarget(Long contactId, MsgChannel channel) {
        Contact contact = getById(contactId);
        if (contact == null) {
            throw new ContactException(ResultInfo.CONTACT_NOT_FOUND);
        }

        return switch (channel) {
            case EMAIL -> contact.getEmail();
            case SMS -> contact.getPhone();
            case MAILBOX -> String.valueOf(contact.getInbox());
        };
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

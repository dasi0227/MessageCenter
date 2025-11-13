package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.context.ContactContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.AccountException;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.MailboxMapper;
import com.dasi.core.service.ContactService;
import com.dasi.core.service.MailboxService;
import com.dasi.pojo.dto.ContactUpdate4MailboxDTO;
import com.dasi.pojo.dto.ContactLoginDTO;
import com.dasi.pojo.dto.MailboxPageDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Mailbox;
import com.dasi.pojo.vo.MailboxLoginVO;
import com.dasi.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MailboxServiceImpl extends ServiceImpl<MailboxMapper, Mailbox> implements MailboxService {

    @Autowired
    private ContactService contactService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Cacheable(
            value = RedisConstant.CACHE_MAILBOX_PREFIX,
            key = "T(String).format('%s:%s:%s', " +
                    "(#dto.isDeleted == 0 ? 'reserve' : 'recycle'), " +
                    "(#dto.orderDesc ? 'desc' : 'asc'), " +
                    "#dto.pageNum)",
            condition = "#dto.pure"
    )
    public PageResult<Mailbox> getMailboxPage(MailboxPageDTO dto) {
        Page<Mailbox> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Mailbox> wrapper = new LambdaQueryWrapper<Mailbox>()
                .eq(Mailbox::getInbox, ContactContextHolder.get().getInbox())
                .eq(dto.getIsRead() != null, Mailbox::getIsRead, dto.getIsRead())
                .eq(dto.getIsDeleted() != null, Mailbox::getIsDeleted, dto.getIsDeleted())
                .like(StrUtil.isNotBlank(dto.getDepartmentName()), Mailbox::getDepartmentName, dto.getDepartmentName())
                .like(StrUtil.isNotBlank(dto.getSubject()), Mailbox::getSubject, dto.getSubject())
                .like(StrUtil.isNotBlank(dto.getContent()), Mailbox::getContent, dto.getContent())
                .orderByDesc(dto.getOrderDesc(), Mailbox::getArrivedAt)
                .orderByAsc(!dto.getOrderDesc(), Mailbox::getArrivedAt);

        Page<Mailbox> result = page(param, wrapper);

        return PageResult.of(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisConstant.CACHE_MAILBOX_PREFIX, allEntries = true)
    public void readMailbox(Long id, Integer isRead) {
        boolean flag = update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsRead, isRead));

        if (!flag) {
            log.warn("【Mailbox Service】更新已读状态失败：id={}, isRead={}", id, isRead);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisConstant.CACHE_MAILBOX_PREFIX, allEntries = true)
    public void deleteMailbox(Long id, Integer isDeleted) {
        boolean flag = update(new LambdaUpdateWrapper<Mailbox>()
                .eq(Mailbox::getId, id)
                .set(Mailbox::getIsDeleted, isDeleted));

        if (!flag) {
            log.warn("【Mailbox Service】更新删除状态失败：id={}, isDeleted={}", id, isDeleted);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisConstant.CACHE_MAILBOX_PREFIX, allEntries = true)
    public void removeMailbox(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Mailbox Service】销毁失败，站内信不存在：id={}", id);
        }
    }

    @Override
    public MailboxLoginVO login(ContactLoginDTO dto) {
        // 查询联系人
        Contact contact = contactService.getOne(new LambdaQueryWrapper<Contact>().eq(Contact::getName, dto.getName()), false);
        if (contact == null) {
            throw new AccountException(ResultInfo.CONTACT_NOT_FOUND);
        }

        // 校验密码
        String password = SecureUtil.md5(dto.getPassword());
        if (!password.equals(contact.getPassword())) {
            throw new AccountException(ResultInfo.CONTACT_PASSWORD_ERROR);
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getContactIdClaimKey(), contact.getId());
        claims.put(jwtProperties.getContactInboxClaimKey(), contact.getInbox());
        String token = jwtUtil.createToken(claims);

        // 构造视图
        MailboxLoginVO vo = BeanUtil.copyProperties(contact, MailboxLoginVO.class);
        vo.setToken(token);

        log.debug("【Contact Service】联系人登陆成功：{}", dto);
        return vo;
    }

    @Override
    @AutoFill(FillType.UPDATE)
    public void updateContact(ContactUpdate4MailboxDTO dto) {
        Long contactId = ContactContextHolder.get().getId();

        boolean flag = contactService.update(new LambdaUpdateWrapper<Contact>()
                .eq(Contact::getId, contactId)
                .set(StrUtil.isNotBlank(dto.getName()) , Contact::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getPassword()) , Contact::getPassword, dto.getPassword())
                .set(StrUtil.isNotBlank(dto.getEmail()) , Contact::getEmail, dto.getEmail())
                .set(StrUtil.isNotBlank(dto.getPhone()) , Contact::getPhone, dto.getPhone())
                .set(Contact::getUpdatedAt, dto.getUpdatedAt())
        );

        if (!flag) {
            log.warn("【Mailbox Service】更新失败，没有记录或值无变化：{}", dto);
        }
    }

}

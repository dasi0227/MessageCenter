package com.dasi.core.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.ContactException;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.pojo.dto.ContactDTO;
import com.dasi.pojo.dto.ContactPageDTO;
import com.dasi.pojo.dto.StatusDTO;
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
    private ContactMapper contactMapper;

    @Autowired
    private InboxUtil inboxUtil;

    @Override
    public void addContact(ContactDTO contactDTO) {
        long count = count(new QueryWrapper<Contact>().eq("name", contactDTO.getName()));
        if (count > 0) {
            throw new ContactException(ResultInfo.CONTACT_ALREADY_EXIST);
        }

        Contact contact = new Contact();
        BeanUtils.copyProperties(contactDTO, contact);
        contact.setInbox(inboxUtil.nextId());
        if (!save(contact)) {
            throw new ContactException(ResultInfo.CONTACT_SAVE_ERROR);
        }

        log.debug("【Contact Service】新增联系人成功：{}", contactDTO);
    }

    @Override
    public void removeContact(Long id) {
        boolean success = removeById(id);
        if (!success) {
            throw new ContactException(ResultInfo.CONTACT_REMOVE_ERROR);
        }
        log.debug("【Contact Service】删除联系人成功：{}", id);
    }

    @Override
    public void updateContact(ContactDTO contactDTO) {
        if (getById(contactDTO.getId()) == null) {
            throw new ContactException(ResultInfo.CONTACT_NOT_EXIST);
        }
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactDTO, contact);
        boolean success = updateById(contact);
        if (!success) {
            throw new ContactException(ResultInfo.CONTACT_UPDATE_ERROR);
        }
        log.debug("【Contact Service】更新联系人成功：{}", contactDTO);
    }

    @Override
    public void updateStatus(StatusDTO statusDTO) {
        boolean success = update(new UpdateWrapper<Contact>()
                .eq("id", statusDTO.getId())
                .set("status", statusDTO.getStatus()));
        if (!success) {
            throw new ContactException(ResultInfo.CONTACT_UPDATE_ERROR);
        }
        log.debug("【Contact Service】更新联系人状态成功：{}", statusDTO);
    }


    @Override
    public PageResult<Contact> getContacts(ContactPageDTO dto) {
        Page<Contact> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<Contact>()
                .like(StrUtil.isNotBlank(dto.getName()), "name", dto.getName())
                .like(StrUtil.isNotBlank(dto.getPhone()), "phone", dto.getPhone())
                .like(StrUtil.isNotBlank(dto.getEmail()), "email", dto.getEmail())
                .eq(dto.getStatus() != null, "status", dto.getStatus());

        if (Boolean.TRUE.equals(dto.getSortedByUpdate())) {
            queryWrapper.orderBy(true, dto.getAsc(), "updated_at");
        } else {
            queryWrapper.orderBy(true, dto.getAsc(), "created_at");
        }

        Page<Contact> contacts = contactMapper.selectPage(page, queryWrapper);
        log.debug("【Contact Service】分页查询联系人成功：{}", dto);
        return PageResult.of(contacts);
    }
}

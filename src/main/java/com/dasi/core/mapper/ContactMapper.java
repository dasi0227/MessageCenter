package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dasi.pojo.entity.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
    @Select("SELECT MAX(inbox) FROM contact")
    Long findMaxInbox();
}

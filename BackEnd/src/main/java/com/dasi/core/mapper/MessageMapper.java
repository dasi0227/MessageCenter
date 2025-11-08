package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dasi.pojo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}

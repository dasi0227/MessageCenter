package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dasi.pojo.entity.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {
}

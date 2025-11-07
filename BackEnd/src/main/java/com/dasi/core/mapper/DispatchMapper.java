package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Mailbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
@SuppressWarnings("MybatisMapperMethodInspection")
public interface DispatchMapper extends BaseMapper<Dispatch> {
    Mailbox selectMailboxInfo(@Param("dispatchId") Long dispatchId);

    List<Map<String, Object>> countByAccount();

    List<Map<String, Object>> countByDepartment();

    List<Map<String, Object>> countByContact();

    List<Map<String, Object>> countByChannel();
}

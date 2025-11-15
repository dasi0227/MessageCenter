package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dasi.pojo.entity.Dispatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DispatchMapper extends BaseMapper<Dispatch> {
    @Select("""
        SELECT d.*
        FROM dispatch d
        LEFT JOIN failure f ON d.id = f.dispatch_id
        WHERE d.status = 'ERROR'
          AND f.id IS NULL
          AND d.finished_at < DATE_SUB(NOW(), INTERVAL 1 HOUR)
    """)
    List<Dispatch> selectErrorWithoutFailure();
}

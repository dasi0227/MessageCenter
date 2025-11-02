package com.dasi.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.MessageDetailVO;
import com.dasi.pojo.vo.MessagePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    IPage<MessagePageVO> selectMessagePage(Page<MessagePageVO> page, @Param("dto") MessagePageDTO dto);

    MessageDetailVO selectMessageDetail(@Param("dispatchId") Long dispatchId);
}

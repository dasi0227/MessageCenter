package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.MessageDetailVO;
import com.dasi.pojo.vo.MessagePageVO;
import jakarta.validation.Valid;

public interface MessageService extends IService<Message> {
    void sendMessage(MessageSendDTO dto);

    PageResult<MessagePageVO> getMessagePage(@Valid MessagePageDTO dto);

    MessageDetailVO getMessageDetail(Long dispatchId);
}

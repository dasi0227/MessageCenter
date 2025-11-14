package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.DispatchPageDTO;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.dto.PromptDTO;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import jakarta.validation.Valid;

public interface MessageService extends IService<Message> {
    void sendMessage(MessageSendDTO dto);

    PageResult<Message> getMessagePage(MessagePageDTO dto);

    PageResult<Dispatch> getMessageDetail(DispatchPageDTO dto);

    String getLlmMessage(@Valid PromptDTO dto);

    Message getMessage(Long id);
}

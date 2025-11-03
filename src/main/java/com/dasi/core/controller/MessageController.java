package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.MessageService;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.vo.MessageDetailVO;
import com.dasi.pojo.vo.MessagePageVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/msg")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Result<Void> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        messageService.sendMessage(dto);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<MessagePageVO>> getMessagePage(@Valid @RequestBody MessagePageDTO dto) {
        PageResult<MessagePageVO> pageResult = messageService.getMessagePage(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/detail/{id}")
    public Result<MessageDetailVO> getMessageDetail(@PathVariable("id") Long dispatchId) {
        MessageDetailVO messageDetailVO = messageService.getMessageDetail(dispatchId);
        return Result.success(messageDetailVO);
    }
}

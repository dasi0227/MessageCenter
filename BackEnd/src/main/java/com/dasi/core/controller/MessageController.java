package com.dasi.core.controller;

import com.dasi.common.annotation.RateLimit;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.properties.ModelProperties;
import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.MessageService;
import com.dasi.pojo.dto.DispatchPageDTO;
import com.dasi.pojo.dto.PromptDTO;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ModelProperties modelProperties;

    @RateLimit(
            key = "#dto.departmentName",
            limit = 5,
            ttl = 10,
            message = "同一部门十秒内不允许发送超过 5 次，请过一会儿后重新尝试！"
    )
    @PostMapping("/send")
    public Result<Void> sendMessage(@Valid @RequestBody MessageSendDTO dto) {
        messageService.sendMessage(dto);
        return Result.success();
    }

    @GetMapping("/channel/list")
    public Result<List<String>> getChannelList() {
        List<String> result = MsgChannel.getChannelList();
        return Result.success(result);
    }

    @GetMapping("/status/list")
    public Result<List<String>> getStatusList() {
        List<String> result = MsgStatus.getStatusList();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Message> getMessage(@PathVariable Long id) {
        Message message = messageService.getMessage(id);
        return Result.success(message);
    }

    @PostMapping("/page")
    public Result<PageResult<Message>> getMessagePage(@Valid @RequestBody MessagePageDTO dto) {
        PageResult<Message> pageResult = messageService.getMessagePage(dto);
        return Result.success(pageResult);
    }

    @PostMapping("/detail")
    public Result<PageResult<Dispatch>> getMessageDetail(@Valid @RequestBody DispatchPageDTO dto) {
        PageResult<Dispatch> result = messageService.getMessageDetail(dto);
        return Result.success(result);
    }

    @RateLimit(
            limit = 10,
            ttl = 10,
            message = "繁忙中，请稍后重试！"
    )
    @PostMapping("/call")
    public Result<String> getLlmMessage(@Valid @RequestBody PromptDTO dto) {
        String result = messageService.getLlmMessage(dto);
        return Result.success(result);
    }

    @GetMapping("/model/list")
    public Result<List<String>> getModelList() {
        List<String> result = modelProperties.getModelList();
        return Result.success(result);
    }
}

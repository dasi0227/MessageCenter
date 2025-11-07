package com.dasi.pojo.dto;

import com.dasi.common.enumeration.MsgChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageSendDTO {
    @NotNull(message = "消息类型不能为空")
    private MsgChannel channel;

    @NotBlank(message = "消息标题不能为空")
    private String subject;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    @NotNull(message = "部门 id 不能为空")
    private Long departmentId;

    @NotEmpty(message = "至少需要一个收件人")
    private List<Long> contactIds;

    private LocalDateTime scheduleAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
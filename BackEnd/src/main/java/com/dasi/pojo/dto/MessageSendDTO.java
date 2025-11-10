package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.enumeration.MsgChannel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageSendDTO {

    private Long            templateId;

    @EnumValid(enumClass = MsgChannel.class)
    @NotNull(message = "消息类型不能为空")
    private MsgChannel      channel;

    @NotBlank(message = "消息标题不能为空")
    private String          subject;

    @NotBlank(message = "消息内容不能为空")
    private String          content;

    private List<String>    attachments;

    // 人信息
    @NotNull(message = "发件人不能为空")
    private Long            departmentId;
    @NotNull(message = "发件人不能为空")
    private String          departmentName;
    @NotEmpty(message = "至少需要一个收件人")
    private List<Long>      contactIds;

    private LocalDateTime   createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime   scheduleAt;
}
package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.MsgChannel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessagePageDTO {
    // 分页参数
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码应该从 1 开始")
    private Long pageNum;
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String subject;
    private String content;

    // 精确查询
    @EnumValid(enumClass = MsgChannel.class)
    private MsgChannel channel;
    private Long accountId;
    private String accountName;
    private Long departmentId;
    private String departmentName;

    // 时间查询
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @NotNull
    private Boolean pure;
}

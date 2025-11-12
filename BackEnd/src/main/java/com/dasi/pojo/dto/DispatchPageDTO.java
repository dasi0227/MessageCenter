package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.MsgStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DispatchPageDTO {
    @NotNull(message = "消息 id 不能为空")
    private Long messageId;

    // 分页参数
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码应该从 1 开始")
    private Long pageNum;
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String subject;
    private String content;

    // 精确查询
    @EnumValid(enumClass = MsgStatus.class)
    private MsgStatus status;
    private Long contactId;
    private String contactName;

    @NotNull
    private Boolean pure;
}
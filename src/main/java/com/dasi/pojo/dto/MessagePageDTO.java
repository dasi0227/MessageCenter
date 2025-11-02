package com.dasi.pojo.dto;

import com.dasi.common.constant.DefaultConstant;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.MsgChannel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessagePageDTO {
    // 分页参数
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码应该从 1 开始")
    private Long pageNum;
    private Long pageSize = DefaultConstant.DEFAULT_PAGE_SIZE;

    // 模糊查询
    private String subject;
    private String content;

    // 精确查询
    private MsgStatus status;
    private MsgChannel channel;
    private Long sendFrom;
    private Long sendTo;
    private String target;

    // 排序规则
    private Boolean sortedByCreated = true;
    private Boolean asc = false;
}

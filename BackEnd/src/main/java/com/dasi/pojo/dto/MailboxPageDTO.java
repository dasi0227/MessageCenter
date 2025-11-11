package com.dasi.pojo.dto;

import com.dasi.common.constant.SystemConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MailboxPageDTO {

    // 分页参数
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码应该从 1 开始")
    private Long pageNum;
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String departmentName;
    private String subject;
    private String content;

    @Min(value = 0, message = "只读状态只能是 0 或 1")
    @Max(value = 1, message = "只读状态只能是 0 或 1")
    private Integer is_read;

    @Min(value = 0, message = "删除状态只能是 0 或 1")
    @Max(value = 1, message = "删除状态只能是 0 或 1")
    private Integer is_deleted;
}
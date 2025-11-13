package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.FailureStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FailurePageDTO {
    // 分页参数
    @NotNull(message = "页码不能为空")
    private Long pageNum;
    @NotNull(message = "页大小不能为空")
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String errorType;
    private String errorMessage;

    // 精确查询
    @EnumValid(enumClass = FailureStatus.class)
    private FailureStatus status;

    // 时间查询
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @NotNull
    private Boolean pure;
}

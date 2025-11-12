package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.enumeration.FailureStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FailureStatusDTO {
    @NotNull(message = "id 不能为空")
    private Long id;
    @EnumValid(enumClass = FailureStatus.class)
    private FailureStatus status;
}

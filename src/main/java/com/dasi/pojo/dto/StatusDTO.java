package com.dasi.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusDTO {

    @NotNull(message = "ID 不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态必须为 0 或 1")
    @Max(value = 1, message = "状态必须为 0 或 1")
    private Integer status;
}

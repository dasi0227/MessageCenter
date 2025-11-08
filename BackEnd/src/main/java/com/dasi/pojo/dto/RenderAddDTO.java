package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RenderAddDTO {
    @NotBlank(message = "字段 name 不能为空")
    private String name;
    @NotBlank(message = "字段 value 不能为空")
    private String value;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

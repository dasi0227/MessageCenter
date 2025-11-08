package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RenderUpdateDTO {
    @NotNull(message = "字段 id 不能为空")
    private Long id;
    @NotBlank(message = "字段 key 不能为空")
    private String name;
    @NotBlank(message = "字段 value 不能为空")
    private String value;
    private String remark;
    private LocalDateTime updatedAt;
}

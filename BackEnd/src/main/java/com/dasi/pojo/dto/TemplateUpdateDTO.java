package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateUpdateDTO {
    @NotNull
    private Long id;
    @NotBlank(message = "模版名称不能为空")
    private String name;
    @NotBlank(message = "标题不能为空")
    private String subject;
    @NotBlank(message = "内容不能为空")
    private String content;
    private LocalDateTime updatedAt;
}

package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateAddDTO {
    @NotBlank(message = "模版名称不能为空")
    private String name;
    @NotBlank(message = "标题不能为空")
    private String subject;
    @NotBlank(message = "内容不能为空")
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
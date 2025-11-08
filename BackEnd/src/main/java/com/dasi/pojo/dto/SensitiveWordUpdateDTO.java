package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensitiveWordUpdateDTO {
    private Long id;
    @NotBlank(message = "敏感词不能为空")
    private String word;
    private LocalDateTime updatedAt;
}

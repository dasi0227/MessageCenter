package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class SensitiveWordsAddDTO {
    @NotEmpty(message = "敏感词不能为空")
    private Set<String> words;
}

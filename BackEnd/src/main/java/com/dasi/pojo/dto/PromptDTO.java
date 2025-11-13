package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PromptDTO {
    @NotBlank
    private String model;

    @NotBlank
    private String prompt;
}

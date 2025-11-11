package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentUpdateDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    private String address;
    private String description;
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    @NotBlank
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;
    private LocalDateTime updatedAt;
}

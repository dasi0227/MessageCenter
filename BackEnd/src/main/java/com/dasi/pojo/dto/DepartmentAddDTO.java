package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentAddDTO {
    @NotBlank(message = "部门名称不能为空")
    private String name;
    @NotBlank(message = "部门地址不能为空")
    private String address;
    @NotBlank(message = "部门描述不能为空")
    private String description;
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    @NotBlank
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

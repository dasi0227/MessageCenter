package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{4,}$", message = "用户名必须为4位字母或数字")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "密码必须为6位字母或数字")
    private String password;

    private String role;
}

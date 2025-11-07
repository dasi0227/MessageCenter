package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.enumeration.AccountRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountAddDTO {
    @NotBlank(message = "账户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{4,}$", message = "用户名必须为 4 位字母或数字")
    private String name;

    @NotBlank(message = "账户密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "密码必须为 6 位字母或数字")
    private String password;

    @EnumValid(enumClass = AccountRole.class)
    private AccountRole role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

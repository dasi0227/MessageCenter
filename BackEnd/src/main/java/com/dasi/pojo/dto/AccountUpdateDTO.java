package com.dasi.pojo.dto;

import com.dasi.common.annotation.EnumValid;
import com.dasi.common.enumeration.AccountRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountUpdateDTO {

    @NotNull(message = "账户ID不能为空")
    private Long id;

    @NotBlank(message = "账户名不能为空")
    private String name;

    @Pattern(regexp = "^$|^[A-Za-z0-9]{6,}$", message = "密码必须为 6 位字母或数字")
    private String password;

    @EnumValid(enumClass = AccountRole.class)
    private AccountRole role;

    private LocalDateTime updatedAt;
}

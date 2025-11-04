package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountPasswordDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "密码必须至少6位字母或数字")
    private String password;
}

package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactUpdate4MailboxDTO {
    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    @Pattern(regexp = "^$|^[A-Za-z0-9]{6,}$", message = "密码必须为 6 位字母或数字")
    private String password;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Pattern(regexp = "^$|^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;

    private LocalDateTime updatedAt;
}

package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Data
public class ContactUpdateDTO {
    @NotNull(message = "联系人 ID 不能为空")
    private Long id;

    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    @Pattern(regexp = "^$|^[A-Za-z0-9]{6,}$", message = "密码必须为 6 位字母或数字")
    private String password;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Pattern(regexp = "^$|^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;

    @Range(min = 0, max = 1, message = "只读状态只能是 0 或 1")
    private Integer status = 1;

    private LocalDateTime updatedAt;
}

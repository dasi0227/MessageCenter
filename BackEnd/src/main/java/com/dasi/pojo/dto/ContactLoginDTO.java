package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactLoginDTO {

    @NotBlank(message = "联系人名不能为空")
    private String name;

    @NotBlank(message = "联系人密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "密码必须为 6 位字母或数字")
    private String password;

}

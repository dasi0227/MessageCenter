package com.dasi.pojo.dto;

import com.dasi.common.annotation.AtLeastOneContact;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AtLeastOneContact(message = "手机号、邮箱至少填写一个")
public class ContactDTO {
    private Long id;

    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Pattern(regexp = "^$|^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status;
}

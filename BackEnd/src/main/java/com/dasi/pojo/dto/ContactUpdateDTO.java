package com.dasi.pojo.dto;

import com.dasi.common.annotation.AtLeastOneContact;
import com.dasi.common.constant.DefaultConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@AtLeastOneContact(message = "手机号、邮箱至少填写一个")
public class ContactUpdateDTO {
    private Long id;

    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    @Nullable
    private String password = DefaultConstant.DEFAULT_PASSWORD;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @Pattern(regexp = "^$|^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "邮箱格式错误")
    private String email;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status = DefaultConstant.DEFAULT_STATUS;

    private LocalDateTime updatedAt;
}

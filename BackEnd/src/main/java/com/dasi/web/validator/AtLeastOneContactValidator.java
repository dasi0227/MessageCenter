package com.dasi.web.validator;

import com.dasi.common.annotation.AtLeastOneContact;
import com.dasi.pojo.dto.ContactAddDTO;
import com.dasi.pojo.dto.ContactUpdateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class AtLeastOneContactValidator implements ConstraintValidator<AtLeastOneContact, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        if (obj == null) {
            return false;
        }

        String phone = null;
        String email = null;

        // 处理 ContactAddDTO
        if (obj instanceof ContactAddDTO dto) {
            phone = dto.getPhone();
            email = dto.getEmail();
        }
        // 处理 ContactUpdateDTO
        else if (obj instanceof ContactUpdateDTO dto) {
            phone = dto.getPhone();
            email = dto.getEmail();
        }
        // 可以继续添加其他支持的类型

        boolean hasPhone = StringUtils.hasText(phone);
        boolean hasEmail = StringUtils.hasText(email);

        return hasPhone || hasEmail;
    }
}

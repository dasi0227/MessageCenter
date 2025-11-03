package com.dasi.web.validator;

import com.dasi.common.annotation.AtLeastOneContact;
import com.dasi.pojo.dto.ContactAddDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class AtLeastOneContactValidator implements ConstraintValidator<AtLeastOneContact, ContactAddDTO> {
    @Override
    public boolean isValid(ContactAddDTO contactAddDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (contactAddDTO == null) {
            return false;
        }

        boolean hasPhone = StringUtils.hasText(contactAddDTO.getPhone());
        boolean hasEmail = StringUtils.hasText(contactAddDTO.getEmail());

        return hasPhone || hasEmail;
    }
}

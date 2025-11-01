package com.dasi.web.validator;

import com.dasi.common.annotation.AtLeastOneContact;
import com.dasi.pojo.dto.ContactDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class AtLeastOneContactValidator implements ConstraintValidator<AtLeastOneContact, ContactDTO> {
    @Override
    public boolean isValid(ContactDTO contactDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (contactDTO == null) {
            return false;
        }

        boolean hasPhone = StringUtils.hasText(contactDTO.getPhone());
        boolean hasEmail = StringUtils.hasText(contactDTO.getEmail());

        return hasPhone || hasEmail;
    }
}

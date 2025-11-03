package com.dasi.web.validator;

import com.dasi.common.annotation.EnumValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    private Set<String> validValues;

    @Override
    public void initialize(EnumValid annotation) {
        validValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name) // 取枚举名，如 ADMIN、USER
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validValues.contains(value);
    }
}
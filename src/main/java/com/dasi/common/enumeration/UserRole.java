package com.dasi.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    SUPER_ADMIN("SUPER_ADMIN"), // 管理
    ADMIN("ADMIN");             // 普通

    @EnumValue
    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
package com.dasi.pojo.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminRole {
    SUPER_ADMIN("SUPER_ADMIN"), // 超级管理员
    ADMIN("ADMIN");             // 普通管理员

    @EnumValue
    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
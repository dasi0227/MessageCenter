package com.dasi.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgStatus {
    PENDING("PENDING"),
    SENDING("SENDING"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    @EnumValue
    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
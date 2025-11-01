package com.dasi.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgStatus {
    SENDING("SENDING"), // 发送中
    SUCCESS("SUCCESS"), // 发送成功
    FAILED("FAILED");   // 发送失败

    @EnumValue
    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
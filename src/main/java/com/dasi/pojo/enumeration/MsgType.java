package com.dasi.pojo.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgType {
    INBOX("INBOX"),
    EMAIL("EMAIL"),
    SMS("SMS");

    @EnumValue
    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
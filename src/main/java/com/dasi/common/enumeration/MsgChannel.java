package com.dasi.common.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.dasi.common.properties.RabbitMqProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MsgChannel {
    INBOX("inbox"),
    EMAIL("email"),
    SMS("sms");

    @EnumValue
    @JsonValue
    private final String value;

    public String getRoute(RabbitMqProperties props) {
        return props.getRoute().get(value);
    }

    public String getQueue(RabbitMqProperties props) {
        return props.getQueue().get(value);
    }
}
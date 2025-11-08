package com.dasi.common.enumeration;

import com.dasi.common.properties.RabbitMqProperties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MsgChannel {
    MAILBOX,
    EMAIL,
    SMS;

    public String getRoute(RabbitMqProperties props) {
        return props.getRoute().get(name().toLowerCase());
    }

    public String getQueue(RabbitMqProperties props) {
        return props.getQueue().get(name().toLowerCase());
    }

    public static List<String> getChannelList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
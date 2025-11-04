package com.dasi.common.enumeration;

import com.dasi.common.properties.RabbitMqProperties;

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
}
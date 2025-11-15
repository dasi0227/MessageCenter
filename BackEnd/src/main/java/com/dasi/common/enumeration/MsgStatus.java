package com.dasi.common.enumeration;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum MsgStatus {
    PENDING,
    SENDING,
    PROCESSING,
    SUCCESS,
    FAIL,
    ERROR;

    public static List<String> getStatusList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static boolean isFinalStatus(MsgStatus status) {
        return EnumSet.of(
                MsgStatus.SUCCESS,
                MsgStatus.FAIL,
                MsgStatus.ERROR
        ).contains(status);
    }
}
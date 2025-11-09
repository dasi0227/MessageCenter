package com.dasi.common.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MsgStatus {
    PENDING,
    SENDING,
    SUCCESS,
    FAIL;

    public static List<String> getStatusList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
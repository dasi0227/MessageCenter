package com.dasi.common.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AccountRole {
    ADMIN,     // 管理
    USER;      // 普通

    public static List<String> getAccountRoleList() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
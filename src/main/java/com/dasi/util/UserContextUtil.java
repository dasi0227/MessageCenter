package com.dasi.util;

public class UserContextUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setUser(Long userId) {
        threadLocal.set(userId);
    }

    public static long getUser() {
        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}

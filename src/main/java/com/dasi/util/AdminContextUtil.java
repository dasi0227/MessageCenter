package com.dasi.util;

public class AdminContextUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setAdmin(Long adminId) {
        threadLocal.set(adminId);
    }

    public static long getAdmin() {
        return threadLocal.get();
    }

    public static void removeAdmin() {
        threadLocal.remove();
    }
}

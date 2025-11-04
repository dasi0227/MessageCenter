package com.dasi.common.context;

public class AccountContextHolder {
    private static final ThreadLocal<AccountContext> threadLocal = new ThreadLocal<>();

    public static void set(AccountContext context) {
        threadLocal.set(context);
    }

    public static AccountContext get() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}

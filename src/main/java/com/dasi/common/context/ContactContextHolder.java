package com.dasi.common.context;

public class ContactContextHolder {
    private static final ThreadLocal<ContactContext> threadLocal = new ThreadLocal<>();

    public static void set(ContactContext context) {
        threadLocal.set(context);
    }

    public static ContactContext get() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}

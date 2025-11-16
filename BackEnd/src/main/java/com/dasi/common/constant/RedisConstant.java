package com.dasi.common.constant;

public class RedisConstant {
    public static final String INBOX_KEY = "inbox:counter";
    public static final String PV_KEY_PATH = "stat:pv:path:";
    public static final String PV_KEY_TIME = "stat:pv:time:";


    public static final String CACHE_ACCOUNT_PREFIX = "cache:account";
    public static final String CACHE_DEPARTMENT_PREFIX = "cache:department";
    public static final String CACHE_CONTACT_PREFIX = "cache:contact";
    public static final String CACHE_DISPATCH_PREFIX = "cache:dispatch";
    public static final String CACHE_MESSAGE_PREFIX = "cache:message";
    public static final String CACHE_RENDER_PREFIX = "cache:render";
    public static final String CACHE_FAILURE_PREFIX = "cache:failure";
    public static final String CACHE_TEMPLATE_PREFIX = "cache:template";
    public static final String CACHE_SENSITIVE_WORD_PREFIX = "cache:sensitiveWord";
    public static final String CACHE_MAILBOX_PREFIX = "cache:mailbox";

    public static final String CACHE_DASHBOARD_PREFIX = "cache:dashboard";

    public static final String RATE_LIMIT_PREFIX = "rateLimit:";
    public static final String RATE_LIMIT_SCRIPT_TEXT =
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local ttl = tonumber(ARGV[2]) " +
            "local current = redis.call('incr', key) " +
            "if current == 1 then " +
            "   redis.call('expire', key, ttl) " +
            "end " +
            "if current > limit then " +
            "   return 0 " +
            "else " +
            "   return 1 " +
            "end"
    ;

    public static final String WECOME_ACCESS_TOKEN_KEY = "wecom:access-token";
}

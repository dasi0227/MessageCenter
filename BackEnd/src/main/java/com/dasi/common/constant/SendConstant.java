package com.dasi.common.constant;

public class SendConstant {
    // ===== 消息验证 =====
    public static final String CONTACT_MISSING = "联系人不存在：";
    public static final String CONTACT_DISABLED = "联系人不可用：";
    public static final String RENDER_UNMATCHED = "渲染失败，存在未定义的占位符：";
    public static final String SENSITIVE_HIT = "命中敏感词：";
    public static final String CONTACT_NOT_FOUND = "联系人不存在：";
    public static final String CONTACT_TARGET_NOT_FOUND = "联系人方式不存在：";

    // ===== 投递相关 =====
    public static final String SEND_MAILBOX_FAIL = "投递站内信失败：";
    public static final String SEND_EMAIL_FAIL = "投递邮件失败：";
    public static final String MQ_SEND_FAIL = "消息队列投递失败：";
    public static final String MAIL_UNAVAILABLE = "找不到当前部门对应的邮件发送器-";

    // ===== 定时发送 =====
    public static final String SCHEDULE_BEFORE_NOW = "定时发送错误：发送时间早于当前时间";
    public static final String SCHEDULE_AFTER_MONTH = "定时发送错误：发送时间间隔超过一个月";
}

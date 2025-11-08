package com.dasi.common.enumeration;

import lombok.Getter;

@Getter
public enum ResultInfo {

    // ====== 系统层 ======
    REQUEST_SUCCESS                 (200, "操作成功"),
    REQUEST_FAIL                    (400, "请求失败"),
    TOKEN_MISSING                   (401, "缺少认证令牌"),
    TOKEN_INVALID                   (402, "无效的令牌"),
    TOKEN_EXPIRED                   (403, "令牌已过期"),
    SERVER_ERROR                    (500, "服务器内部错误"),
    DATABASE_ERROR                  (501, "数据库操作异常"),
    PARAM_VALIDATE_FAIL             (502, "参数校验失败"),

    ACCOUNT_PERMISSION_DENIED       (10, "账户权限不足"),
    ACCOUNT_NOT_FOUND               (10, "账户不存在"),
    ACCOUNT_PASSWORD_ERROR          (10, "账户密码错误"),
    ACCOUNT_NAME_ALREADY_EXISTS     (10, ""),
    ACCOUNT_REMOVE_ADMIN_DENIED     (10, "禁止删除管理员账户"),
    ACCOUNT_UPDATE_ADMIN_DENIED     (10, ""),
    ACCOUNT_REMOVE_CURRENT_DENIED   (10, ""),
    ACCOUNT_UPDATE_CURRENT_DENIED   (10, ""),
    CONTACT_NOT_FOUND               (10, "联系人不存在"),
    CONTACT_PASSWORD_ERROR          (10, "联系人密码错误"),
    CONTACT_NAME_ALREADY_EXISTS     (10, "联系人名称已存在"),
    RENDER_UPDATE_FAIL              (10, "系统预设字段不可修改"),
    RENDER_REMOVE_FAIL              (10, "系统预设字段不可删除"),
    RENDER_KEY_NOT_FOUND            (10, "渲染失败：存在未定义的占位符"),
    RENDER_SYS_VALUE_MISSING        (10, "渲染失败：系统变量取值缺失"),
    RENDER_PLACEHOLDER_UNRESOLVED   (10, "渲染失败：存在未解析占位符"),
    UNIQUE_FIELD_CONFLICT           (10, "名字已存在"),
    SEND_MAILBOX_FAIL               (8001, "站内信发送失败"),
    SEND_SMS_FAIL                   (8002, "短信发送失败"),
    SEND_EMAIL_FAIL                 (8003, "邮件发送失败"),
    UNKNOWN_ERROR                   (9999, "未知错误"),;

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
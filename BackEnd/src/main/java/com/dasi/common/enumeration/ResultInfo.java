package com.dasi.common.enumeration;

import lombok.Getter;

@Getter
public enum ResultInfo {

    // ====== 系统层 ======
    REQUEST_SUCCESS(200, "操作成功"),
    REQUEST_FAIL(400, "请求失败"),
    TOKEN_MISSING(401, "缺少认证令牌"),
    TOKEN_INVALID(402, "无效的令牌"),
    TOKEN_EXPIRED(403, "令牌已过期"),
    ACCESS_FORBIDDEN(404, "无访问权限"),
    RESOURCE_NOT_FOUND(405, "资源未找到"),
    SERVER_ERROR(500, "服务器内部错误"),
    DATABASE_ERROR(501, "数据库操作异常"),
    PARAM_VALIDATE_FAIL(502, "参数校验失败"),

    // ====== 账户模块 ======
    ACCOUNT_NOT_FOUND(1001, "账户不存在"),
    ACCOUNT_ALREADY_EXIST(1002, "账户已存在"),
    ACCOUNT_PASSWORD_ERROR(1003, "账户密码错误"),
    ACCOUNT_INFO_ERROR(1004, "账户信息异常"),
    ACCOUNT_SAVE_ERROR(1005, "账户创建失败"),
    ACCOUNT_REMOVE_ERROR(1006, "账户删除失败"),
    ACCOUNT_UPDATE_ERROR(1007, "账户修改失败"),
    ACCOUNT_PERMISSION_DENIED(1008, "账户权限不足"),
    ACCOUNT_REMOVE_UPDATE_DENIED(1009, "禁止删除或修改当前登录账户"),
    ACCOUNT_REMOVE_DENIED(1010, "禁止删除管理员账户"),

    // ====== 部门模块 ======
    DEPARTMENT_ALREADY_EXISTS(2001, "部门已存在"),
    DEPARTMENT_UPDATE_FAIL(2002, "部门修改失败"),
    DEPARTMENT_REMOVE_FAIL(2003, "部门删除失败"),

    // ====== 联系人模块 ======
    CONTACT_NOT_FOUND(2001, "联系人不存在"),
    CONTACT_ALREADY_EXISTS(2002, "联系人已存在"),
    CONTACT_REMOVE_FAIL(2003, "联系人删除失败"),
    CONTACT_UPDATE_FAIL(2004, "联系人修改失败"),
    CONTACT_PASSWORD_ERROR(2005, "联系人密码错误"),

    // ====== 站内信模块 ======
    MAILBOX_UPDATE_FAIL(3001, "站内信修改失败"),
    MAILBOX_REMOVE_FAIL(3002, "站内信删除失败"),

    // ====== 消息发送模块 ======
    SEND_MAILBOX_ERROR(4001, "站内信发送失败"),
    SEND_SMS_ERROR(4002, "短信发送失败"),
    SEND_EMAIL_ERROR(4003, "邮件发送失败"),

    // ====== 敏感词模块 ======
    SENSITIVE_WORD_ALREADY_EXISTS(5001, "敏感词已存在"),
    SENSITIVE_WORD_NOT_FOUND(5002, "敏感词不存在"),
    SENSITIVE_WORD_REMOVE_FAIL(5003, "敏感词删除失败"),
    SENSITIVE_WORD_UPDATE_FAIL(5004, "敏感词修改失败"),
    TEMPLATE_NOT_FOUND(5005, "模版不存在"),
    TEMPLATE_ALREADY_EXIST(5006, "模版已存在"),
    TEMPLATE_REMOVE_FAIL(5007, "模版删除失败"),
    TEMPLATE_UPDATE_FAIL(5008, "模版修改失败"),

    // ====== 兜底 ======
    UNKNOWN_ERROR(9999, "未知错误");

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
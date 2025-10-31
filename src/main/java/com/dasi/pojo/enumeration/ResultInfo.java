package com.dasi.pojo.enumeration;

import lombok.Getter;

@Getter
public enum ResultInfo {
    // 处理层
    SUCCESS(200, "执行成功"),
    FAIL(400, "请求错误"),
    TOKEN_MISSING(401, "令牌缺失"),
    TOKEN_ERROR(402, "令牌错误"),
    TOKEN_EXPIRED(403, "令牌过期"),
    FORBIDDEN(404, "未经授权"),
    NOT_FOUND(405, "请求无效"),
    SERVER_ERROR(500, "执行错误"),

    // 业务层
    USER_NOT_EXIST(1001, "用户名不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    CONTACT_NOT_EXIST(2001, "联系人不存在"),
    MESSAGE_SEND_FAILED(3001, "信息发送错误");

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

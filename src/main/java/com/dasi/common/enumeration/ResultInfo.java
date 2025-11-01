package com.dasi.common.enumeration;

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
    SERVER_ERROR(500, "服务器错误"),
    DATABASE_ERROR(501, "数据库错误"),
    PARAM_INFO_ERROR(502, "参数错误"),

    // 业务层
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    USER_DISABLED(1003, "用户被禁用"),
    USER_REGISTER_ERROR(1004, "用户注册错误"),
    USER_PASSWORD_ERROR(1005, "用户密码错误"),
    USER_INFO_ERROR(1006, "用户信息错误"),
    USER_SAVE_ERROR(1007, "用户添加错误"),
    USER_REMOVE_ERROR(1008, "用户删除错误"),
    USER_UPDATE_ERROR(1009, "用户更新错误"),
    USER_PERMISSION_DENIED(1010, "用户权限不足"),

    CONTACT_NOT_EXIST(2001, "联系人不存在"),
    CONTACT_ALREADY_EXIST(2002, "联系人已存在"),
    CONTACT_INFO_ERROR(2003, "联系人信息错误"),
    CONTACT_SAVE_ERROR(2004, "联系人添加错误"),
    CONTACT_REMOVE_ERROR(2005, "联系人删除错误"),
    CONTACT_UPDATE_ERROR(2006, "联系人更新错误"),

    BOTTOM(9999, "无用信息");

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

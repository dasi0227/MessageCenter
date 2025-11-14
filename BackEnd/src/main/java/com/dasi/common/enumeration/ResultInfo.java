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
    DATABASE_ERROR                  (501, "MySQL 错误"),
    REDIS_ERROR                     (502, "Redis 错误"),
    RATE_LIMIT_ERROR                (503, "限流错误"),
    PARAM_VALIDATE_FAIL             (504, "参数校验失败"),
    PATH_VALIDATE_FAIL              (505, "路径校验失败"),
    CALL_LLM_ERROR                  (506, "调用大模型错误"),

    ACCOUNT_PERMISSION_DENIED       (1001, "账户权限不足"),
    ACCOUNT_NOT_FOUND               (1002, "账户不存在"),
    ACCOUNT_PASSWORD_ERROR          (1003, "账户密码错误"),
    ACCOUNT_NAME_ALREADY_EXISTS     (1004, "账户名称已存在"),
    ACCOUNT_REMOVE_ADMIN_DENIED     (1005, "不可删除管理员账户"),
    ACCOUNT_UPDATE_ADMIN_DENIED     (1006, "禁止修改管理员账户"),
    ACCOUNT_REMOVE_CURRENT_DENIED   (1007, "不可删除当前登录账户"),
    ACCOUNT_UPDATE_CURRENT_DENIED   (1008, "不可修改当前登录账户"),
    CONTACT_NOT_FOUND               (1009, "联系人不存在"),
    CONTACT_PASSWORD_ERROR          (1010, "联系人密码错误"),
    CONTACT_NAME_ALREADY_EXISTS     (1011, "联系人名称已存在"),
    RENDER_UPDATE_FAIL              (1012, "系统预设字段不可修改"),
    RENDER_REMOVE_FAIL              (1013, "系统预设字段不可删除"),
    UNIQUE_FIELD_CONFLICT           (1014, "名称已存在"),
    MESSAGE_NOT_FOUND               (1015, "消息不存在"),
    UNKNOWN_ERROR                   (1999, "未知错误");

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
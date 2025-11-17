package com.dasi.common.enumeration;

import lombok.Getter;

@Getter
public enum ResultInfo {

    REQUEST_SUCCESS                 (200, "操作成功"),
    REQUEST_FAIL                    (400, "请求失败"),
    TOKEN_ERROR                     (401, "令牌无效"),
    PATH_VALIDATE_FAIL              (500, "路径校验失败"),
    FILE_UPLOAD_FAIL                (501, "文件上传失败"),

    JVM_ERROR                       (600, "JVM 错误"),
    MYSQL_ERROR                     (601, "MySQL 错误"),
    REDIS_ERROR                     (602, "Redis 错误"),
    LLM_ERROR                       (603, "AI 模型调用错误"),
    OSS_ERROR                       (604, "云存储错误"),
    MQ_ERROR                        (605, "消息队列错误"),
    RATE_ERROR                      (606, "限流错误"),
    PARAM_ERROR                     (607, "传参错误"),

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
    FILE_TOO_LARGE                  (1016, "上传文件过大，不允许超过 1 MB"),
    FILE_NOT_FOUND                  (1017, "文件不存在"),
    FILE_DOWNLOAD_ERROR             (1018, "文件下载失败");

    private final int code;
    private final String message;

    ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
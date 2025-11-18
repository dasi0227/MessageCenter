DROP DATABASE IF EXISTS message_center;

CREATE DATABASE IF NOT EXISTS message_center
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE message_center;

-- 账户
DROP TABLE IF EXISTS account;
CREATE TABLE IF NOT EXISTS account (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '账户名',
    password    VARCHAR(32)     NOT NULL                    COMMENT '账户密码',
    role        VARCHAR(32)     NOT NULL                    COMMENT '账户角色',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 部门
DROP TABLE IF EXISTS department;
CREATE TABLE department (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '部门ID',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '部门名称',
    address     VARCHAR(128)    NOT NULL UNIQUE             COMMENT '部门地址',
    description VARCHAR(256)    NOT NULL UNIQUE             COMMENT '部门描述',
    phone       VARCHAR(32)     DEFAULT NULL                COMMENT '手机号',
    email       VARCHAR(128)    DEFAULT NULL                COMMENT '邮箱',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 联系人
DROP TABLE IF EXISTS contact;
CREATE TABLE IF NOT EXISTS contact (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '联系人名',
    password    VARCHAR(32)     NOT NULL                    COMMENT '联系人密码',
    inbox       BIGINT          NOT NULL UNIQUE             COMMENT '信箱号',
    phone       VARCHAR(32)     DEFAULT NULL                COMMENT '手机号',
    email       VARCHAR(128)    DEFAULT NULL                COMMENT '邮箱',
    status      TINYINT         NOT NULL DEFAULT 1          COMMENT '状态',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 模板
DROP TABLE IF EXISTS template;
CREATE TABLE IF NOT EXISTS template (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(64)     NOT NULL                    COMMENT '模板名称',
    subject     VARCHAR(128)    DEFAULT NULL                COMMENT '标题',
    content     TEXT            NOT NULL                    COMMENT '正文',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS sensitive_word;
CREATE TABLE IF NOT EXISTS sensitive_word (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    word        VARCHAR(64)     NOT NULL UNIQUE             COMMENT '敏感词',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS render;
CREATE TABLE IF NOT EXISTS render (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(64)     NOT NULL UNIQUE             COMMENT '字段键（占位符 key）',
    value       VARCHAR(256)    DEFAULT NULL                COMMENT '字段值',
    remark      VARCHAR(256)    DEFAULT NULL                COMMENT '备注说明',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息
DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message (
    id               BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '雪花 id',
    template_id      BIGINT          DEFAULT NULL                COMMENT '模版 id',
    channel          VARCHAR(32)     NOT NULL                    COMMENT '消息渠道',
    subject          VARCHAR(255)    NOT NULL                    COMMENT '消息标题',
    content          TEXT            DEFAULT NULL                COMMENT '消息正文内容',
    attachments      JSON            DEFAULT NULL                COMMENT '附件 URL 列表',
    account_id       BIGINT          NOT NULL                    COMMENT '操作人 id',
    account_name     VARCHAR(64)     NOT NULL                    COMMENT '操作人姓名',
    department_id    BIGINT          NOT NULL                    COMMENT '发件人 id',
    department_name  VARCHAR(64)     NOT NULL                    COMMENT '发件人姓名',
    contact_ids      JSON            NOT NULL                    COMMENT '收件人 id 列表',
    created_at       DATETIME        NOT NULL                    COMMENT '创建时间',
    schedule_at      DATETIME        DEFAULT NULL                COMMENT '定时时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 发送
DROP TABLE IF EXISTS dispatch;
CREATE TABLE IF NOT EXISTS dispatch (
    id                BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '雪花 id',
    message_id        BIGINT          NOT NULL                    COMMENT '消息 id',
    subject           VARCHAR(255)    NOT NULL                    COMMENT '消息标题（渲染后）',
    content           TEXT            DEFAULT NULL                COMMENT '消息正文内容（渲染后）',
    attachments       JSON            DEFAULT NULL                COMMENT '附件 URL 列表',
    schedule_at       DATETIME        DEFAULT NULL                COMMENT '定时时间',
    department_id     BIGINT          NOT NULL                    COMMENT '发件人 id',
    department_name   VARCHAR(64)     NOT NULL                    COMMENT '发件人姓名',
    contact_id        BIGINT          NOT NULL                    COMMENT '收件人 id',
    contact_name      VARCHAR(64)     NOT NULL                    COMMENT '收件人姓名',
    target            VARCHAR(255)    DEFAULT NULL                COMMENT '收件目标',
    status            VARCHAR(32)     NOT NULL                    COMMENT '消息状态',
    error_msg         VARCHAR(512)    DEFAULT NULL                COMMENT '错误信息',
    created_at        DATETIME        NOT NULL                    COMMENT '创建时间',
    sent_at           DATETIME        DEFAULT NULL                COMMENT '发送时间',
    process_at        DATETIME        DEFAULT NULL                COMMENT '处理时间',
    finished_at       DATETIME        DEFAULT NULL                COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS failure;
CREATE TABLE failure (
    id              BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    dispatch_id     BIGINT          NOT NULL                    COMMENT '分发 id',
    error_type      VARCHAR(255)    DEFAULT NULL                COMMENT '异常类型',
    error_message   VARCHAR(1024)   DEFAULT NULL                COMMENT '异常信息',
    error_stack     LONGTEXT        DEFAULT NULL                COMMENT '调用栈',
    status          VARCHAR(32)     NOT NULL                    COMMENT '错误状态',
    created_at      DATETIME        NOT NULL                    COMMENT '创建时间',
    resolved_at     DATETIME        DEFAULT NULL                COMMENT '解决时间',
    payload         JSON            NOT NULL                    COMMENT '原始 Dispatch'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 信箱
DROP TABLE IF EXISTS mailbox;
CREATE TABLE IF NOT EXISTS mailbox (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    inbox       BIGINT          NOT NULL                    COMMENT '信箱号',
    department_name VARCHAR(32) NOT NULL                    COMMENT '发件人名',
    subject     VARCHAR(128)    NOT NULL                    COMMENT '消息标题',
    content     TEXT            NOT NULL                    COMMENT '消息内容',
    attachments JSON                                        COMMENT '消息附件',
    is_read     TINYINT         DEFAULT 0                   COMMENT '是否已读：0=未读，1=已读',
    is_deleted  TINYINT         DEFAULT 0                   COMMENT '是否删除：0=正常，1=删除',
    arrived_at  DATETIME        NOT NULL                    COMMENT '到达时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS oss_file;
CREATE TABLE oss_file (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    message_id  BIGINT          DEFAULT NULL                COMMENT '消息 id',
    file_name   VARCHAR(64)     NOT NULL                    COMMENT '文件原始名字',
    object_name VARCHAR(128)    NOT NULL                    COMMENT '存储对象名字',
    url         VARCHAR(512)    NOT NULL UNIQUE             COMMENT '云存储路径',
    used        TINYINT         NOT NULL DEFAULT 0          COMMENT '是否使用',
    uploaded_by BIGINT          NOT NULL                    COMMENT '上传账号',
    uploaded_at DATETIME        NOT NULL                    COMMENT '上传时间',
    sent_at     DATETIME        DEFAULT NULL                COMMENT '发送时间',
    clear_at    DATETIME        DEFAULT NULL                COMMENT '清理时间'
);
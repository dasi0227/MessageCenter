DROP DATABASE IF EXISTS message_center;

CREATE DATABASE IF NOT EXISTS message_center
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE message_center;

-- 账户
CREATE TABLE IF NOT EXISTS account (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '账户名',
    password    VARCHAR(32)     NOT NULL                    COMMENT '账户密码',
    role        VARCHAR(32)     NOT NULL                    COMMENT '账户角色',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 联系人
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

-- 联系人组表
CREATE TABLE IF NOT EXISTS contact_group (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(64)     NOT NULL UNIQUE             COMMENT '组名',
    description VARCHAR(255)    DEFAULT NULL                COMMENT '组描述',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 联系
CREATE TABLE IF NOT EXISTS relation (
    group_id   BIGINT NOT NULL COMMENT '组 id',
    contact_id BIGINT NOT NULL COMMENT '联系人 id',
    PRIMARY KEY (group_id, contact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 模板
CREATE TABLE IF NOT EXISTS template (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(64)     NOT NULL                    COMMENT '模板名称',
    subject     VARCHAR(128)    DEFAULT NULL                COMMENT '标题',
    content     TEXT            NOT NULL                    COMMENT '正文',
    created_by  BIGINT          NOT NULL                    COMMENT '创建人 id',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息
CREATE TABLE IF NOT EXISTS message (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    subject     VARCHAR(128)    NOT NULL                    COMMENT '消息标题',
    content     TEXT            NOT NULL                    COMMENT '消息内容',
    attachments MEDIUMTEXT                                  COMMENT '消息附件',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 发送
CREATE TABLE IF NOT EXISTS dispatch (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    msg_id      BIGINT          NOT NULL                    COMMENT '消息 id',
    send_from   BIGINT          NOT NULL                    COMMENT '发件人 id',
    send_to     BIGINT          NOT NULL                    COMMENT '收件人 id',
    target      VARCHAR(255)    NOT NULL                    COMMENT '收件人地址',
    status      VARCHAR(32)     NOT NULL                    COMMENT '消息状态',
    error_msg   VARCHAR(255)    DEFAULT NULL                COMMENT '错误信息',
    schedule_at DATETIME        DEFAULT NULL                COMMENT '定时时间',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    sent_at     DATETIME        DEFAULT NULL                COMMENT '发送时间',
    finished_at DATETIME        DEFAULT NULL                COMMENT '完成时间',
    UNIQUE KEY uk_msg_contact (msg_id, send_from, send_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 信箱
CREATE TABLE IF NOT EXISTS mailbox (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    inbox       BIGINT          NOT NULL                    COMMENT '信箱号',
    sender      VARCHAR(64)     NOT NULL                    COMMENT '发件人名',
    subject     VARCHAR(128)    NOT NULL                    COMMENT '消息标题',
    content     TEXT            NOT NULL                    COMMENT '消息内容',
    attachments MEDIUMTEXT                                  COMMENT '消息附件',
    is_read     TINYINT         DEFAULT 0                   COMMENT '是否已读：0=未读，1=已读',
    is_delete   TINYINT         DEFAULT 0                   COMMENT '是否删除：0=正常，1=删除',
    reach_at    DATETIME        NOT NULL                    COMMENT '到达时间',
    read_at     DATETIME        DEFAULT NULL                COMMENT '阅读时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
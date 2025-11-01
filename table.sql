CREATE DATABASE IF NOT EXISTS message_center
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE message_center;

-- 用户
CREATE TABLE IF NOT EXISTS user (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '全局唯一自增 id',
    username     VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    password     VARCHAR(128) NOT NULL COMMENT '密码，加密存储',
    role         VARCHAR(32) NOT NULL DEFAULT 'user' COMMENT '超级/普通',
    inbox        CHAR(6) NOT NULL UNIQUE COMMENT '站内信箱 id，整数自增字符串',
    status       TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 通讯录
CREATE TABLE IF NOT EXISTS contact (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '全局唯一自增 id',
    name        VARCHAR(64) NOT NULL COMMENT '联系人姓名',
    phone       VARCHAR(20) DEFAULT NULL COMMENT '手机号，仅支持中国大陆 +86 格式',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱地址',
    inbox       CHAR(6) DEFAULT NULL COMMENT '站内信箱 id，整数递增字符串',
    status      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_inbox (inbox)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录表';

-- 消息
CREATE TABLE IF NOT EXISTS msg (
    id             VARCHAR(64) PRIMARY KEY COMMENT '全局唯一 id',
    type           VARCHAR(32) NOT NULL COMMENT '消息类型：INBOX=站内，SMS=短信，EMAIL=邮件',
    status         VARCHAR(32) DEFAULT NULL COMMENT '消息状态：草稿=NULL，发送中=SENDING，成功=SUCCESS，失败=FAIL',
    subject        VARCHAR(128) DEFAULT NULL COMMENT '消息标题',
    content        TEXT NOT NULL COMMENT '消息正文内容',
    send_by        BIGINT NOT NULL COMMENT '发信人 id',
    error_msg      VARCHAR(255) DEFAULT NULL COMMENT '错误信息（发送失败时记录）',
    is_delete      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除，1=删除，0=正常',
    is_draft       TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否草稿，1=草稿，0=正常',
    schedule_at    DATETIME DEFAULT NULL COMMENT '定时发送时间，为空表示立即发送',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    sent_at        DATETIME DEFAULT NULL COMMENT '进入发送队列的时间',
    finished_at    DATETIME DEFAULT NULL COMMENT '消息发送完成时间',
    CONSTRAINT fk_msg_user FOREIGN KEY (send_by) REFERENCES user(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    KEY idx_msg_type (type),
    KEY idx_msg_status (status),
    KEY idx_msg_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 消息收件人映射
CREATE TABLE IF NOT EXISTS msg_to (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '全局唯一自增 id',
    msg_id      VARCHAR(64) NOT NULL COMMENT '消息 id',
    contact_id  BIGINT NOT NULL COMMENT '收件人 id',
    CONSTRAINT fk_to_msg FOREIGN KEY (msg_id) REFERENCES msg(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_to_contact FOREIGN KEY (contact_id) REFERENCES contact(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE KEY uk_msg_contact (msg_id, contact_id),
    KEY idx_to_msg (msg_id),
    KEY idx_to_contact (contact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息收件人映射';

# DROP TABLE msg_to, msg, contact, user;

INSERT INTO user (username, password, role, inbox, status)
VALUES ('dasi', '90c0bd850970a1dc69cd4a297de3c300', 'SUPER_ADMIN', '000000', 1);

DELETE FROM user WHERE username != 'dasi';
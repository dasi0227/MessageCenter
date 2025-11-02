CREATE DATABASE IF NOT EXISTS message_center
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE message_center;

-- 用户
CREATE TABLE IF NOT EXISTS user (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '全局唯一自增 id',
    username     VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    password     VARCHAR(128) NOT NULL COMMENT '密码，加密存储',
    role         VARCHAR(32) NOT NULL DEFAULT 'ADMIN' COMMENT '超级/普通',
    status       TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 通讯录
CREATE TABLE IF NOT EXISTS contact (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '全局唯一自增 id',
    name        VARCHAR(64) NOT NULL COMMENT '联系人姓名',
    phone       VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    inbox       BIGINT NOT NULL UNIQUE COMMENT '站内信箱',
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录表';

DROP TABLE message, dispatch;

-- 消息
CREATE TABLE IF NOT EXISTS message (
    id             BIGINT PRIMARY KEY COMMENT '全局唯一 id',
    channel        VARCHAR(32) NOT NULL COMMENT '消息渠道：INBOX=站内，SMS=短信，EMAIL=邮件',
    subject        VARCHAR(128) DEFAULT NULL COMMENT '消息标题',
    content        TEXT NOT NULL COMMENT '消息内容',
    is_delete      TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除，1=删除，0=正常',
    schedule_at    DATETIME DEFAULT NULL COMMENT '定时发送时间，为空表示立即发送',
    KEY idx_msg_channel (channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 发送
CREATE TABLE IF NOT EXISTS dispatch (
    id          BIGINT PRIMARY KEY COMMENT '全局唯一自增 id',
    msg_id      VARCHAR(64) NOT NULL COMMENT '消息 id',
    send_from   BIGINT NOT NULL COMMENT '发件人 id',
    send_to     BIGINT NOT NULL COMMENT '收件人 id',
    target      VARCHAR(255) NOT NULL COMMENT '收件人地址',
    status      VARCHAR(32) DEFAULT 'PENDING' COMMENT '消息状态：待发送=PENDING，发送中=SENDING，成功=SUCCESS，失败=FAIL',
    error_msg   VARCHAR(255) DEFAULT NULL COMMENT '错误信息（发送失败时记录）',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    sent_at     DATETIME DEFAULT NULL COMMENT '进入发送队列的时间',
    finished_at DATETIME DEFAULT NULL COMMENT '消息发送完成时间',
    UNIQUE KEY uk_msg_contact (msg_id, send_from, send_to),
    KEY idx_msg_id (msg_id),
    KEY idx_send_from (send_from),
    KEY idx_send_to (send_to),
    KEY idx_target (target),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发送表';

# INSERT INTO user (username, password, role, status)
# VALUES ('dasi', '90c0bd850970a1dc69cd4a297de3c300', 'SUPER_ADMIN', '000000', 1);

# DELETE FROM user WHERE username != 'dasi';
package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.enumeration.MsgStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("dispatch")
public class Dispatch implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long            id;             // 雪花 id

    private Long            accountId;      // 操作人 id
    private Long            msgId;          // 消息 id
    private Long            departmentId;   // 部门 id
    private Long            contactId;      // 收件人 id
    private String          target;         // 收件目标

    private MsgChannel      channel;        // 消息渠道
    private MsgStatus       status;         // 消息状态
    private String          errorMsg;       // 错误信息

    private LocalDateTime   scheduleAt;     // 定时时间
    private LocalDateTime   createdAt;      // 创建时间
    private LocalDateTime   sentAt;         // 发送时间
    private LocalDateTime   finishedAt;     // 完成时间
}

/*
CREATE TABLE IF NOT EXISTS dispatch (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    msg_id      BIGINT          NOT NULL                    COMMENT '消息 id',
    account_id  BIGINT          NOT NULL                    COMMENT '操作人 id',
    send_from   BIGINT          NOT NULL                    COMMENT '发件人 id',
    send_to     BIGINT          NOT NULL                    COMMENT '收件人 id',
    target      VARCHAR(32)     NOT NULL                    COMMENT '收件人地址',
    channel     VARCHAR(32)     NOT NULL                    COMMENT '消息渠道',
    status      VARCHAR(32)     NOT NULL                    COMMENT '消息状态',
    error_msg   VARCHAR(256)    DEFAULT NULL                COMMENT '错误信息',
    schedule_at DATETIME        DEFAULT NULL                COMMENT '定时时间',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    sent_at     DATETIME        DEFAULT NULL                COMMENT '发送时间',
    finished_at DATETIME        DEFAULT NULL                COMMENT '完成时间',
    UNIQUE KEY uk_msg_contact (msg_id, send_from, send_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/
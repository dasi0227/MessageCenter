package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dasi.common.enumeration.MsgStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "dispatch", autoResultMap = true)
public class Dispatch implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long            id;             // 雪花 id

    // 冗余消息信息
    @JsonSerialize(using = ToStringSerializer.class)
    private Long            messageId;      // 消息 id
    private String          subject;        // 消息标题（渲染）
    private String          content;        // 消息正文内容（渲染）
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String>    attachments;    // 附件 URL
    private Long            departmentId;   // 发件人 id
    private String          departmentName; // 发件人姓名
    private LocalDateTime   createdAt;      // 创建时间

    // 收件人信息
    private Long            contactId;      // 收件人 id
    private String          contactName;    // 收件人姓名
    private String          target;         // 收件目标

    // 派送信息
    private MsgStatus       status;         // 消息状态
    private String          errorMsg;       // 错误信息
    private LocalDateTime   sentAt;         // 发送时间
    private LocalDateTime   finishedAt;     // 完成时间
}

/*
CREATE TABLE IF NOT EXISTS dispatch (
    id                BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '雪花 id',
    message_id        BIGINT          NOT NULL                    COMMENT '消息 id',
    subject           VARCHAR(255)    NOT NULL                    COMMENT '消息标题（渲染后）',
    content           TEXT            DEFAULT NULL                COMMENT '消息正文内容（渲染后）',
    attachments       JSON            DEFAULT NULL                COMMENT '附件 URL 列表',
    department_id     BIGINT          NOT NULL                    COMMENT '发件人 id',
    department_name   VARCHAR(64)     NOT NULL                    COMMENT '发件人姓名',
    created_at        DATETIME        NOT NULL                    COMMENT '创建时间',
    contact_id        BIGINT          NOT NULL                    COMMENT '收件人 id',
    contact_name      VARCHAR(64)     NOT NULL                    COMMENT '收件人姓名',
    target            VARCHAR(255)    DEFAULT NULL                COMMENT '收件目标',
    status            VARCHAR(32)     NOT NULL                    COMMENT '消息状态',
    error_msg         VARCHAR(512)    DEFAULT NULL                COMMENT '错误信息',
    sent_at           DATETIME        DEFAULT NULL                COMMENT '发送时间',
    finished_at       DATETIME        DEFAULT NULL                COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息派发表';
*/
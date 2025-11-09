package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dasi.common.enumeration.MsgChannel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "message", autoResultMap = true)
public class Message {

    // 消息信息
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long            id;             // 雪花 id
    private Long            templateId;     // 模版 id
    private MsgChannel      channel;        // 消息渠道
    private String          subject;        // 消息标题
    private String          content;        // 消息正文内容
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String>    attachments;    // 附件 URL

    // 人信息
    private Long            accountId;      // 操作人 id
    private String          accountName;    // 操作人姓名
    private Long            departmentId;   // 发件人 id
    private String          departmentName; // 发件人姓名
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long>      contactIds;     // 收件人列表

    // 时间信息
    private LocalDateTime   createdAt;      // 创建时间
    private LocalDateTime   scheduleAt;     // 定时时间

}

/*
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息主体表';
 */
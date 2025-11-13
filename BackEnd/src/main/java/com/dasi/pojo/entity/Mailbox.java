package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
@TableName(value = "mailbox", autoResultMap = true)
public class Mailbox {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long inbox;
    private String departmentName;
    private String subject;
    private String content;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachments;
    private Integer isRead;
    private Integer isDeleted;
    private LocalDateTime arrivedAt;
}
/*
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
 */
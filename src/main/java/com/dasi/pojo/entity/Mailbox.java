package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mailbox")
public class Mailbox {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inbox;
    private String addresser;
    private String subject;
    private String content;
    private String attachments;
    private Integer is_read;
    private Integer is_deleted;
    private LocalDateTime arrivedAt;
    private LocalDateTime readAt;

}
/*
CREATE TABLE IF NOT EXISTS mailbox (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    inbox       BIGINT          NOT NULL                    COMMENT '信箱号',
    addresser   VARCHAR(32)     NOT NULL                    COMMENT '发件人名',
    subject     VARCHAR(128)    NOT NULL                    COMMENT '消息标题',
    content     TEXT            NOT NULL                    COMMENT '消息内容',
    attachments MEDIUMTEXT                                  COMMENT '消息附件',
    is_read     TINYINT         DEFAULT 0                   COMMENT '是否已读：0=未读，1=已读',
    is_deleted  TINYINT         DEFAULT 0                   COMMENT '是否删除：0=正常，1=删除',
    arrived_at  DATETIME        NOT NULL                    COMMENT '到达时间',
    read_at     DATETIME        DEFAULT NULL                COMMENT '阅读时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */
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
@TableName("message")
public class Message {

    @TableId(type = IdType.ASSIGN_ID)
    private Long            id;             // 雪花 id
    private String          subject;        // 消息标题
    private String          content;        // 消息正文内容
    private String          attachments;    // 附件 URL
    private LocalDateTime   createdAt;      // 创建时间

}

/*
-- 消息
CREATE TABLE IF NOT EXISTS message (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    subject     VARCHAR(128)    NOT NULL                    COMMENT '消息标题',
    content     TEXT            NOT NULL                    COMMENT '消息内容',
    attachments MEDIUMTEXT                                  COMMENT '消息附件',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */
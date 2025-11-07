package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("template")
public class Template {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

/*
CREATE TABLE IF NOT EXISTS template (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(64)     NOT NULL                    COMMENT '模板名称',
    subject     VARCHAR(128)    DEFAULT NULL                COMMENT '标题',
    content     TEXT            NOT NULL                    COMMENT '正文',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */

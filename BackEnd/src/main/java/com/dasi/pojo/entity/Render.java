package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("render")
public class Render {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("`key`")
    private String key;
    @TableField("`value`")
    private String value;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

/*
CREATE TABLE IF NOT EXISTS render (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    `key`       VARCHAR(64)     NOT NULL UNIQUE             COMMENT '字段键（占位符 key）',
    `value`     VARCHAR(256)    DEFAULT NULL                COMMENT '字段值',
    remark      VARCHAR(256)    DEFAULT NULL                COMMENT '备注说明',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */
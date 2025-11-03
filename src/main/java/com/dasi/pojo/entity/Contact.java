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
@TableName("contact")
public class Contact {

    @TableId(type = IdType.AUTO)
    private Long            id;             // 自增 id
    private String          name;           // 联系人姓名
    private String          password;       // 联系人姓名
    private Integer         status;         // 状态
    private String          phone;          // 手机号
    private String          email;          // 邮箱地址
    private Long            inbox;          // 信箱号
    private LocalDateTime   createdAt;      // 创建时间
    private LocalDateTime   updatedAt;      // 更新时间

}

/*
-- 联系人
CREATE TABLE IF NOT EXISTS contact (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '联系人名',
    password    VARCHAR(32)     NOT NULL                    COMMENT '联系人密码',
    inbox       BIGINT          NOT NULL UNIQUE             COMMENT '信箱号',
    phone       VARCHAR(32)     DEFAULT NULL                COMMENT '手机号',
    email       VARCHAR(128)    DEFAULT NULL                COMMENT '邮箱',
    status      TINYINT         NOT NULL DEFAULT 1          COMMENT '状态',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL                    COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */
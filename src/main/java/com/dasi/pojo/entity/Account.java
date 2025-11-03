package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dasi.common.enumeration.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("account")
public class Account {

    @TableId(type = IdType.AUTO)
    private Long            id;             // 自增 id
    private String          name;           // 账户名
    private String          password;       // 账户密码
    private AccountRole     role;           // 账户角色
    private LocalDateTime   createdAt;      // 创建时间

}

/*
-- 账户
CREATE TABLE IF NOT EXISTS account (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    name        VARCHAR(32)     NOT NULL UNIQUE             COMMENT '账户名',
    password    VARCHAR(32)     NOT NULL                    COMMENT '账户密码',
    role        VARCHAR(32)     NOT NULL                    COMMENT '账户角色',
    created_at  DATETIME        NOT NULL                    COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/
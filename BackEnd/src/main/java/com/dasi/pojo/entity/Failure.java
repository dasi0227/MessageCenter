package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dasi.common.enumeration.FailureStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("failure")
public class Failure {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dispatchId;
    private String errorType;
    private String errorMessage;
    private String errorStack;
    private FailureStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Dispatch payload;
}

/*
CREATE TABLE failure (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '自增 id',
    dispatch_id     BIGINT          NOT NULL                    COMMENT '分发 id',
    error_type      VARCHAR(255)    DEFAULT NULL                COMMENT '异常类型',
    error_message   VARCHAR(1024)   DEFAULT NULL                COMMENT '异常信息',
    error_stack     LONGTEXT        DEFAULT NULL                COMMENT '调用栈',
    status          VARCHAR(32)     NOT NULL                    COMMENT '错误状态',
    created_at      DATETIME        NOT NULL                    COMMENT '创建时间',
    resolved_at     DATETIME        DEFAULT NULL                COMMENT '解决时间',
    payload         JSON            NOT NULL                    COMMENT '原始 Dispatch'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
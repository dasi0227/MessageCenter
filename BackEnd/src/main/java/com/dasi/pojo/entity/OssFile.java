package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@TableName("oss_file")
public class OssFile {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;
    private String url;
    private String name;
    private Integer used;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private LocalDateTime sentAt;
}

/*
CREATE TABLE oss_file (
    id          BIGINT          PRIMARY KEY                 COMMENT '雪花 id',
    message_id  BIGINT          DEFAULT NULL                COMMENT '消息 id',
    name        VARCHAR(64)     NOT NULL                    COMMENT '文件原始名字',
    url         VARCHAR(512)    NOT NULL UNIQUE             COMMENT '云存储路径',
    used        TINYINT         NOT NULL DEFAULT 0          COMMENT '是否使用',
    uploaded_by BIGINT          NOT NULL                    COMMENT '上传账号',
    uploaded_at DATETIME        NOT NULL                    COMMENT '上传时间',
    sent_at     DATETIME        DEFAULT NULL                COMMENT '发送时间'
);
 */

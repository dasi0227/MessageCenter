package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dasi.common.enumeration.MsgChannel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                    // 全局唯一 id
    private MsgChannel channel;         // 消息类型：INBOX / SMS / EMAIL
    private String subject;             // 消息标题
    private String content;             // 消息正文内容
    private Integer isDelete;           // 是否删除：1=删除，0=正常
    private LocalDateTime scheduleAt;   // 定时发送时间，为空表示立即发送
}
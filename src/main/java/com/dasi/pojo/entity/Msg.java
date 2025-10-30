package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dasi.pojo.enumeration.MsgStatus;
import com.dasi.pojo.enumeration.MsgType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("msg")
public class Msg {
    @TableId
    private String id;                // 全局唯一 id

    private MsgType type;             // 消息类型：INBOX / SMS / EMAIL
    private MsgStatus status;         // 消息状态：SENDING / SUCCESS / FAILED
    private String subject;           // 消息标题
    private String content;           // 消息正文内容
    private Long sendBy;              // 发信人 id
    private String errorMsg;          // 错误信息

    private Integer isDelete;         // 是否删除：1=删除，0=正常
    private Integer isDraft;          // 是否草稿：1=草稿，0=正常

    private LocalDateTime scheduleAt; // 定时发送时间
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
    private LocalDateTime sentAt;     // 进入发送队列时间
    private LocalDateTime finishedAt; // 消息发送完成时间
}

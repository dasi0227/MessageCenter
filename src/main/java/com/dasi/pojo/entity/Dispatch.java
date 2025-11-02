package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dasi.common.enumeration.MsgStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("dispatch")
public class Dispatch {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;                 // 全局唯一 id

    private Long msgId;            // 消息 id
    private Long sendFrom;           // 发件人 id
    private Long sendTo;             // 收件人 id
    private String target;           // 收件人地址：信箱号、手机号、邮箱号

    private MsgStatus status;        // 消息状态：PENDING / SENDING / SUCCESS / FAIL
    private String errorMsg;         // 错误信息（发送失败时记录）

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime sentAt;    // 进入发送队列的时间
    private LocalDateTime finishedAt;// 消息发送完成时间
}
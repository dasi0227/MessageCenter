package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("msg_to")
public class MsgTo {

    @TableId(type = IdType.AUTO)
    private Long id;         // 全局唯一自增 id

    private String msgId;    // 消息 id
    private Long contactId;  // 收件人 id
}
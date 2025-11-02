package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contact")
public class Contact {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;              // 联系人姓名
    private Integer status;           // 状态：1=启用，0=禁用

    private String phone;             // 手机号
    private String email;             // 邮箱地址
    private Long inbox;               // 站内信箱 id

    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}

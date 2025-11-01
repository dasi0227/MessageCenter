package com.dasi.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dasi.common.enumeration.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;                 // 全局唯一自增 id

    private String username;         // 用户名
    private String password;         // 密码：加密存储
    private UserRole role;           // 角色：ADMIN / BASIC
    private String inbox;            // 收件箱 id
    private Integer status;          // 状态：1=启用，0=禁用

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}

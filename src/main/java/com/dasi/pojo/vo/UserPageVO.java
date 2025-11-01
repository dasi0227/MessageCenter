package com.dasi.pojo.vo;

import com.dasi.common.enumeration.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageVO {
    private Long id;
    private String username;
    private UserRole role;
    private Integer status;

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}
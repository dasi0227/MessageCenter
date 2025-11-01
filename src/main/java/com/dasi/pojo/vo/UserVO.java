package com.dasi.pojo.vo;

import com.dasi.common.enumeration.UserRole;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private UserRole role;
    private Integer status;
    private String inbox;

    private String token;
}
package com.dasi.pojo.vo;

import com.dasi.common.enumeration.AccountRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountPageVO {
    private Long            id;
    private String          name;
    private AccountRole     role;
    private LocalDateTime   createdAt;
}
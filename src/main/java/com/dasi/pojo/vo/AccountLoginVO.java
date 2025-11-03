package com.dasi.pojo.vo;

import com.dasi.common.enumeration.AccountRole;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
public class AccountLoginVO {
    private Long            id;
    private String          name;
    private AccountRole     role;
    private LocalDateTime   createdAt;
    private String          token;
}
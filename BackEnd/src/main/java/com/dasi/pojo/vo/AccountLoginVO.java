package com.dasi.pojo.vo;

import com.dasi.common.enumeration.AccountRole;
import lombok.Data;

@Data
public class AccountLoginVO {
    private Long            id;
    private String          name;
    private AccountRole     role;
    private String          token;
}
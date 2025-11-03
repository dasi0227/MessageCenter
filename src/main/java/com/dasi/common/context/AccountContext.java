package com.dasi.common.context;

import com.dasi.common.enumeration.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountContext {
    private Long id;
    private AccountRole role;
}
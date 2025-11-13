package com.dasi.pojo.dto;

import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.AccountRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountPageDTO {

    // 分页参数
    @NotNull(message = "页码不能为空")
    private Long pageNum;
    @NotNull(message = "页大小不能为空")
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String name;

    // 精确查询
    private AccountRole role;

    @NotNull
    private Boolean pure;
}

package com.dasi.pojo.dto;

import com.dasi.common.constant.SystemConstant;
import com.dasi.common.enumeration.AccountRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountPageDTO {

    // 分页参数
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码应该从 1 开始")
    private Long pageNum;
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String name;

    // 精确查询
    private AccountRole role;
}

package com.dasi.pojo.dto;

import com.dasi.common.constant.SystemConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentPageDTO {
    // 分页参数
    @NotNull(message = "页码不能为空")
    private Long pageNum;
    @NotNull(message = "页大小不能为空")
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String name;
    private String description;
    private String address;

    @NotNull
    private Boolean pure;
}
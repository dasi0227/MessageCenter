package com.dasi.pojo.dto;

import com.dasi.common.constant.SystemConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ContactPageDTO {

    // 分页参数
    @NotNull(message = "页码不能为空")
    private Long pageNum;
    @NotNull(message = "页大小不能为空")
    private Long pageSize = SystemConstant.PAGE_SIZE;

    // 模糊查询
    private String name;
    private String phone;
    private String email;

    // 精确查询
    @Range(min = 0, max = 1, message = "只读状态只能是 0 或 1")
    private Integer status;

    @NotNull
    private Boolean pure;
}

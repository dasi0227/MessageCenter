package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Data
public class ContactStatusDTO {
    @NotNull(message = "id 不能为空")
    private Long id;
    @Range(min = 0, max = 1, message = "只读状态只能是 0 或 1")
    private Integer status;
    private LocalDateTime updatedAt;
}

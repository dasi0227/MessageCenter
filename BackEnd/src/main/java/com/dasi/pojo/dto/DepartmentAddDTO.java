package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentAddDTO {
    @NotBlank(message = "部门名称不能为空")
    private String name;
    @NotBlank(message = "部门地址不能为空")
    private String address;
    @NotBlank(message = "部门描述不能为空")
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

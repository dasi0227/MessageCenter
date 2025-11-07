package com.dasi.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentUpdateDTO {
    @NotNull
    private Long id;
    private String name;
    private String address;
    private String description;
    private LocalDateTime updatedAt;
}

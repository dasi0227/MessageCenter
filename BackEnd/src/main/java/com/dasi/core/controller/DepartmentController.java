package com.dasi.core.controller;

import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.DepartmentService;
import com.dasi.pojo.dto.DepartmentAddDTO;
import com.dasi.pojo.dto.DepartmentPageDTO;
import com.dasi.pojo.dto.DepartmentUpdateDTO;
import com.dasi.pojo.entity.Department;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/page")
    public Result<PageResult<Department>> getDepartmentPage(@Valid @RequestBody DepartmentPageDTO dto) {
        PageResult<Department> result = departmentService.getDepartmentPage(dto);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<Void> addDepartment(@Valid @RequestBody DepartmentAddDTO dto) {
        departmentService.addDepartment(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateDepartment(@Valid @RequestBody DepartmentUpdateDTO dto) {
        departmentService.updateDepartment(dto);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeDepartment(@PathVariable("id") Long id) {
        departmentService.removeDepartment(id);
        return Result.success();
    }
}

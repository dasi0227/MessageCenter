package com.dasi.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.DepartmentAddDTO;
import com.dasi.pojo.dto.DepartmentPageDTO;
import com.dasi.pojo.dto.DepartmentUpdateDTO;
import com.dasi.pojo.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {
    PageResult<Department> getDepartmentPage(DepartmentPageDTO dto);

    void addDepartment(DepartmentAddDTO dto);

    void updateDepartment(DepartmentUpdateDTO dto);

    void removeDepartment(Long id);

    List<Department> getDepartmentList();
}

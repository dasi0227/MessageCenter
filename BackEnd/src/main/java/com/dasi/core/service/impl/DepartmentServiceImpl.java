package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.DepartmentException;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.DepartmentMapper;
import com.dasi.core.service.DepartmentService;
import com.dasi.pojo.dto.DepartmentAddDTO;
import com.dasi.pojo.dto.DepartmentPageDTO;
import com.dasi.pojo.dto.DepartmentUpdateDTO;
import com.dasi.pojo.entity.Department;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Override
    public PageResult<Department> getDepartmentPage(DepartmentPageDTO dto) {
        Page<Department> pageParam = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>()
                .like(StrUtil.isNotBlank(dto.getName()), Department::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getAddress()), Department::getAddress, dto.getAddress())
                .like(StrUtil.isNotBlank(dto.getDescription()), Department::getDescription, dto.getDescription())
                .orderByDesc(Department::getCreatedAt);

        Page<Department> result = page(pageParam, wrapper);

        log.debug("【Department Service】分页查询部门：{}", dto);
        return PageResult.of(result);
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public void addDepartment(DepartmentAddDTO dto) {
        if (exists(new LambdaQueryWrapper<Department>().eq(Department::getName, dto.getName()))) {
            throw new DepartmentException(ResultInfo.DEPARTMENT_ALREADY_EXISTS);
        }

        Department department = BeanUtil.copyProperties(dto, Department.class);
        save(department);

        log.debug("【Department Service】新增部门：{}", dto);
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(DepartmentUpdateDTO dto) {
        if (!update(new LambdaUpdateWrapper<Department>()
                .eq(Department::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Department::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getAddress()), Department::getAddress, dto.getAddress())
                .set(StrUtil.isNotBlank(dto.getDescription()), Department::getDescription, dto.getDescription())
                .set(Department::getUpdatedAt, dto.getUpdatedAt()))) {
            throw new DepartmentException(ResultInfo.DEPARTMENT_UPDATE_FAIL);
        }

        log.debug("【Department Service】更新部门：{}", dto);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void removeDepartment(Long id) {
        if (!removeById(id)) {
            throw new DepartmentException(ResultInfo.DEPARTMENT_REMOVE_FAIL);
        }

        log.debug("【Department Service】删除部门：{}", id);
    }
}

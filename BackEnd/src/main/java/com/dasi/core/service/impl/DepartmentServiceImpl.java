package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.enumeration.FillType;
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

import java.util.List;

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

        return PageResult.of(result);
    }


    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @UniqueField(fieldName = "name")
    public void addDepartment(DepartmentAddDTO dto) {
        Department department = BeanUtil.copyProperties(dto, Department.class);
        boolean flag = save(department);

        if (!flag) {
            log.warn("【Department Service】新增部门失败：{}", dto);
        }
    }


    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @UniqueField(fieldName = "name")
    public void updateDepartment(DepartmentUpdateDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<Department>()
                .eq(Department::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Department::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getAddress()), Department::getAddress, dto.getAddress())
                .set(StrUtil.isNotBlank(dto.getDescription()), Department::getDescription, dto.getDescription())
                .set(StrUtil.isNotBlank(dto.getPhone()), Department::getPhone, dto.getPhone())
                .set(StrUtil.isNotBlank(dto.getEmail()), Department::getEmail, dto.getEmail())
                .set(Department::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Department Service】更新失败，没有记录或值无变化：{}", dto);
        }
    }


    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void removeDepartment(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Department Service】删除失败，部门不存在：{}", id);
        }
    }

    @Override
    public List<Department> getDepartmentList() {
        return list();
    }
}

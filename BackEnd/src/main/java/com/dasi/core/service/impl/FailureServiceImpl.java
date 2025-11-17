package com.dasi.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.enumeration.FailureStatus;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.FailureMapper;
import com.dasi.core.service.FailureService;
import com.dasi.pojo.dto.FailurePageDTO;
import com.dasi.pojo.dto.FailureStatusDTO;
import com.dasi.pojo.entity.Failure;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FailureServiceImpl extends ServiceImpl<FailureMapper, Failure> implements FailureService {

    @Override
    @Cacheable(
            value = RedisConstant.CACHE_FAILURE_PREFIX,
            key = "'page:' + #dto.pageNum",
            condition = "#dto.pure"
    )
    public PageResult<Failure> getFailurePage(FailurePageDTO dto) {
        Page<Failure> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Failure> wrapper = new LambdaQueryWrapper<Failure>()
                .eq(dto.getStatus() != null, Failure::getStatus, dto.getStatus())
                .like(StrUtil.isNotBlank(dto.getErrorType()), Failure::getErrorType, dto.getErrorType())
                .like(StrUtil.isNotBlank(dto.getErrorMessage()), Failure::getErrorMessage, dto.getErrorMessage())
                .ge(dto.getStartTime() != null, Failure::getCreatedAt, dto.getStartTime())
                .le(dto.getEndTime() != null, Failure::getCreatedAt, dto.getEndTime())
                .orderByDesc(Failure::getCreatedAt);

        Page<Failure> result = page(param, wrapper);

        return PageResult.of(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @CacheEvict(value = RedisConstant.CACHE_FAILURE_PREFIX, allEntries = true)
    public void updateStatus(FailureStatusDTO dto) {
        update(new LambdaUpdateWrapper<Failure>()
                .eq(Failure::getId, dto.getId())
                .set(dto.getStatus() != null, Failure::getStatus, dto.getStatus())
                .set(dto.getStatus().equals(FailureStatus.RESOLVED), Failure::getResolvedAt, LocalDateTime.now())
                .set(!dto.getStatus().equals(FailureStatus.RESOLVED), Failure::getResolvedAt, null)
        );
    }

    @Override
    public Long getUnsolvedNum() {
        return count(new LambdaQueryWrapper<Failure>().ne(Failure::getStatus, FailureStatus.RESOLVED));
    }
}

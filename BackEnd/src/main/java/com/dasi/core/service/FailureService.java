package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.FailurePageDTO;
import com.dasi.pojo.dto.FailureStatusDTO;
import com.dasi.pojo.entity.Failure;
import jakarta.validation.Valid;

public interface FailureService extends IService<Failure> {

    void updateStatus(@Valid FailureStatusDTO dto);

    PageResult<Failure> getFailurePage(@Valid FailurePageDTO dto);

    Long getUnsolvedNum();
}

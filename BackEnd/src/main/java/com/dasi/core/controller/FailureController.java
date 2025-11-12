package com.dasi.core.controller;

import com.dasi.common.enumeration.FailureStatus;
import com.dasi.common.result.PageResult;
import com.dasi.common.result.Result;
import com.dasi.core.service.FailureService;
import com.dasi.pojo.dto.FailurePageDTO;
import com.dasi.pojo.dto.FailureStatusDTO;
import com.dasi.pojo.entity.Failure;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/failure")
public class FailureController {

    @Autowired
    private FailureService failureService;

    @PostMapping("/page")
    public Result<PageResult<Failure>> getFailurePage(@Valid @RequestBody FailurePageDTO dto) {
        PageResult<Failure> result = failureService.getFailurePage(dto);
        return Result.success(result);
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody FailureStatusDTO dto) {
        failureService.updateStatus(dto);
        return Result.success();
    }

    @GetMapping("/status/list")
    public Result<List<String>> getStatusList() {
        List<String> list = FailureStatus.getStatusList();
        return Result.success(list);
    }

    @GetMapping("/num")
    public Result<Long> getUnsolvedNum() {
        Long num = failureService.getUnsolvedNum();
        return Result.success(num);
    }

}

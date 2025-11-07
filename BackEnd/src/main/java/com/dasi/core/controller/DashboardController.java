package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.DashboardService;
import com.dasi.pojo.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/total")
    public Result<StatTotalVO> getStatTotal() {
        StatTotalVO vo = dashboardService.getStatTotal();
        return Result.success(vo);
    }

    @GetMapping("/contact")
    public Result<StatContactVO> getStatContact() {
        StatContactVO vo = dashboardService.getStatContact();
        return Result.success(vo);
    }

    @GetMapping("/channel")
    public Result<StatChannelVO> getStatChannel() {
        StatChannelVO vo = dashboardService.getStatChannel();
        return Result.success(vo);
    }

    @GetMapping("/account")
    public Result<StatAccountVO> getStatAccount() {
        StatAccountVO vo = dashboardService.getStatAccount();
        return Result.success(vo);
    }

    @GetMapping("/year")
    public Result<StatYearVO> getStatYear() {
        StatYearVO vo = dashboardService.getStatYear();
        return Result.success(vo);
    }

    @GetMapping("/month")
    public Result<StatMonthVO> getStatWeek() {
        StatMonthVO vo = dashboardService.getStatMonth();
        return Result.success(vo);
    }
}

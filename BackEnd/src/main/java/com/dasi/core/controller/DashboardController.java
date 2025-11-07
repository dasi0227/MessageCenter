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

    @GetMapping("/num")
    public Result<StatNumVO> getStatNum() {
        StatNumVO vo = dashboardService.getStatNum();
        return Result.success(vo);
    }

    @GetMapping("/dispatch")
    public Result<StatDispatchVO> getStatDispatch() {
        StatDispatchVO vo = dashboardService.getStatDispatch();
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

package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @PostMapping("/flush/{entity}")
    public Result<Void> flush(@PathVariable("entity") String entity) {
        systemService.flush(entity);
        return Result.success();
    }
}

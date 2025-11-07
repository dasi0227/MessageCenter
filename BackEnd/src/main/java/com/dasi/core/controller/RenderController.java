package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.RenderService;
import com.dasi.pojo.entity.Render;
import com.dasi.pojo.dto.RenderAddDTO;
import com.dasi.pojo.dto.RenderUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/render")
public class RenderController {

    @Autowired
    private RenderService renderService;

    @GetMapping("/list")
    public Result<List<Render>> list() {
        List<Render> list = renderService.getRenderList();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody RenderAddDTO dto) {
        renderService.addRender(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody RenderUpdateDTO dto) {
        renderService.updateRender(dto);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeRender(@PathVariable("id") Long id) {
        renderService.removeRender(id);
        return Result.success();
    }

}

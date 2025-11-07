package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.TemplateService;
import com.dasi.pojo.dto.TemplateAddDTO;
import com.dasi.pojo.dto.TemplateUpdateDTO;
import com.dasi.pojo.entity.Template;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/list")
    public Result<List<Template>> list() {
        List<Template> list = templateService.getTemplateList();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Void> addTemplate(@Valid @RequestBody TemplateAddDTO dto) {
        templateService.addTemplate(dto);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeTemplate(@PathVariable("id") Long id) {
        templateService.removeTemplate(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateTemplate(@Valid @RequestBody TemplateUpdateDTO dto) {
        templateService.updateTemplate(dto);
        return Result.success();
    }
}

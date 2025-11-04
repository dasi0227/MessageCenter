package com.dasi.core.controller;

import com.dasi.common.result.Result;
import com.dasi.core.service.SensitiveWordService;
import com.dasi.pojo.dto.SensitiveWordUpdateDTO;
import com.dasi.pojo.dto.SensitiveWordsAddDTO;
import com.dasi.pojo.entity.SensitiveWord;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensitive")
public class SensitiveWordController {

    @Autowired
    SensitiveWordService sensitiveWordService;

    @GetMapping("/list")
    public Result<List<SensitiveWord>> getSensitiveWordList() {
        List<SensitiveWord> list = sensitiveWordService.getSensitiveWordList();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Void> addSensitiveWords(@Valid @RequestBody SensitiveWordsAddDTO dto) {
        sensitiveWordService.addSensitiveWords(dto);
        return Result.success();
    }

    @PostMapping("/remove/{id}")
    public Result<Void> removeSensitiveWord(@PathVariable("id") String id) {
        sensitiveWordService.removeSensitiveWord(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> updateSensitiveWord(@Valid @RequestBody SensitiveWordUpdateDTO dto) {
        sensitiveWordService.updateSensitiveWord(dto);
        return Result.success();
    }
}

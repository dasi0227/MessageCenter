package com.dasi.core.controller;

import com.dasi.common.annotation.RateLimit;
import com.dasi.common.properties.ModelProperties;
import com.dasi.common.result.Result;
import com.dasi.core.service.SystemService;
import com.dasi.pojo.dto.PromptDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private ModelProperties modelProperties;

    @PostMapping("/flush/{entity}")
    public Result<Void> flush(@PathVariable("entity") String entity) {
        systemService.flush(entity);
        return Result.success();
    }

    @RateLimit(
            limit = 10,
            ttl = 10,
            message = "繁忙中，请稍后重试！"
    )
    @PostMapping("/llm")
    public Result<String> getLlmMessage(@Valid @RequestBody PromptDTO dto) {
        String result = systemService.getLlmMessage(dto);
        return Result.success(result);
    }

    @GetMapping("/models")
    public Result<List<String>> getModelList() {
        List<String> result = modelProperties.getModelList();
        return Result.success(result);
    }

}

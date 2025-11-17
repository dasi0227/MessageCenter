package com.dasi.core.controller;

import com.dasi.common.annotation.RateLimit;
import com.dasi.common.result.Result;
import com.dasi.core.service.OssFileService;
import com.dasi.pojo.dto.FileNameListDTO;
import com.dasi.pojo.dto.OssFileDownloadDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/oss")
public class OssFileController {

    @Autowired
    private OssFileService ossFileService;

    @RateLimit(
            limit = 10,
            ttl = 10,
            message = "繁忙中，请稍后重试！"
    )
    @PostMapping("/upload")
    public Result<String> uploadFile(@NotNull @RequestParam("file") MultipartFile file) {
        String result = ossFileService.uploadFile(file);
        return Result.success(result);
    }

    @RateLimit(
            limit = 10,
            ttl = 10,
            message = "繁忙中，请稍后重试！"
    )
    @PostMapping("/download")
    public void downloadFile(@RequestBody OssFileDownloadDTO dto, HttpServletResponse response) {
        ossFileService.downloadFile(dto, response);
    }

    @PostMapping("/name")
    public Result<List<String>> getFileNameList(@Valid @RequestBody FileNameListDTO dto) {
        List<String> result = ossFileService.getFileNameList(dto);
        return Result.success(result);
    }

}

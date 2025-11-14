package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.dto.FileNameListDTO;
import com.dasi.pojo.entity.OssFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OssFileService extends IService<OssFile> {
    String uploadFile(@NotNull MultipartFile file);

    List<String> getFileNameList(@Valid FileNameListDTO dto);
}

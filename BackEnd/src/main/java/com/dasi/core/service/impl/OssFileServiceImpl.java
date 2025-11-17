package com.dasi.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.core.mapper.OssFileMapper;
import com.dasi.core.service.OssFileService;
import com.dasi.pojo.dto.FileNameListDTO;
import com.dasi.pojo.dto.OssFileDownloadDTO;
import com.dasi.pojo.entity.OssFile;
import com.dasi.util.AliOssUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements OssFileService {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.getSize() > 1024 * 1024) {
            throw new MessageCenterException(ResultInfo.FILE_TOO_LARGE);
        }

        try {
            String fileName = file.getOriginalFilename();
            String objectName = aliOssUtil.createObjectName(fileName);
            String url = aliOssUtil.putObject(file.getBytes(), objectName);
            OssFile ossFile = OssFile.builder()
                    .url(url)
                    .fileName(fileName)
                    .objectName(objectName)
                    .used(0)
                    .uploadedBy(AccountContextHolder.get().getId())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            save(ossFile);
            log.debug("【OssFile Service】上传文件成功：{}", url);
            return url;
        } catch (IOException e) {
            throw new MessageCenterException(ResultInfo.FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public List<String> getFileNameList(FileNameListDTO dto) {
        List<String> urls = Optional.ofNullable(dto.getUrls()).orElse(Collections.emptyList());
        if (urls.isEmpty()) {
            return Collections.emptyList();
        }
        List<OssFile> files = list(new LambdaQueryWrapper<OssFile>().in(OssFile::getUrl, dto.getUrls()));
        Map<String, String> url2name = files.stream().collect(Collectors.toMap(OssFile::getUrl, OssFile::getFileName));
        return dto.getUrls().stream().map(url -> url2name.getOrDefault(url, "未命名文件")).collect(Collectors.toList());
    }

    @Override
    public void downloadFile(OssFileDownloadDTO dto, HttpServletResponse response) {
        OssFile file = lambdaQuery().eq(OssFile::getUrl, dto.getUrl()).one();

        if (file == null) {
            throw new MessageCenterException(ResultInfo.FILE_NOT_FOUND);
        }

        String objectName = file.getObjectName();
        String encodedName = URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8);
        byte[] bytes = aliOssUtil.getObject(objectName);

        try {
            response.resetBuffer();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
            response.setContentLength(bytes.length);
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            log.debug("【OssFile Service】下载文件成功：{}", dto.getUrl());
        } catch (IOException e) {
            throw new MessageCenterException(ResultInfo.FILE_DOWNLOAD_ERROR);
        }
    }

}

package com.dasi.core.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.core.mapper.OssFileMapper;
import com.dasi.core.service.OssFileService;
import com.dasi.pojo.dto.FileNameListDTO;
import com.dasi.pojo.entity.OssFile;
import com.dasi.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
            String objectName = createObjectName(fileName);
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            OssFile ossFile = OssFile.builder()
                    .url(url)
                    .name(fileName)
                    .used(0)
                    .uploadedBy(AccountContextHolder.get().getId())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            save(ossFile);

            return url;
        } catch (IOException e) {
            throw new MessageCenterException(ResultInfo.FILE_UPLOAD_FAIL);
        }
    }

    private String createObjectName(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timePrefix = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString(true);
        String ext = "";
        if (fileName != null && fileName.contains(".")) {
            ext = fileName.substring(fileName.lastIndexOf('.'));
        }
        return "test/"  + datePath + "/" + timePrefix + "-" + uuid + ext;
    }

    @Override
    public List<String> getFileNameList(FileNameListDTO dto) {
        List<String> urls = Optional.ofNullable(dto.getUrls()).orElse(Collections.emptyList());
        if (urls.isEmpty()) {
            return Collections.emptyList();
        }
        List<OssFile> files = list(new LambdaQueryWrapper<OssFile>().in(OssFile::getUrl, dto.getUrls()));
        Map<String, String> url2name = files.stream().collect(Collectors.toMap(OssFile::getUrl, OssFile::getName));
        return dto.getUrls().stream().map(url -> url2name.getOrDefault(url, "未命名文件")).collect(Collectors.toList());
    }

}

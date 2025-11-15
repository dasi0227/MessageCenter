package com.dasi.core.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dasi.core.service.OssFileService;
import com.dasi.pojo.entity.OssFile;
import com.dasi.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ClearOssFileTask {

    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private AliOssUtil aliOssUtil;

    @Scheduled(cron = "0 * * * * ?")
    public void clearUnused() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        List<OssFile> files = ossFileService.list(
                new LambdaQueryWrapper<OssFile>()
                        .eq(OssFile::getUsed, 0)
                        .lt(OssFile::getUploadedAt, oneHourAgo)
        );

        files.forEach(ossFile -> {
            aliOssUtil.deleteObject(ossFile.getObjectName());
            ossFileService.removeById(ossFile.getId());
        });

        log.debug("【ClearOssFileTask】发现未使用文件 {} 个", files.size());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearExpired() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<OssFile> files = ossFileService.list(
                new LambdaQueryWrapper<OssFile>()
                        .eq(OssFile::getUsed, 1)
                        .isNull(OssFile::getClearAt)
                        .lt(OssFile::getSentAt, sevenDaysAgo)
        );

        files.forEach(ossFile -> {
            aliOssUtil.deleteObject(ossFile.getObjectName());
            ossFile.setClearAt(LocalDateTime.now());
            ossFileService.updateById(ossFile);
        });

        log.debug("【ClearOssFileTask】发现过期文件 {} 个", files.size());
    }

}

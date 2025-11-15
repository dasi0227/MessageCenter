package com.dasi.util;

import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dasi.common.properties.AliOssProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class AliOssUtil {

    @Autowired
    private AliOssProperties aliOssProperties;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret()
        );
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    public String putObject(byte[] bytes, String objectName) {
        ossClient.putObject(aliOssProperties.getBucket(), objectName, new ByteArrayInputStream(bytes));
        String url = "https://" + aliOssProperties.getBucket() + "." + aliOssProperties.getEndpoint() + "/" + objectName;
        log.debug("文件上传到：{}", url);
        return url;
    }

    public String createObjectName(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timePrefix = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString(true);
        String ext = "";
        if (fileName != null && fileName.contains(".")) {
            ext = fileName.substring(fileName.lastIndexOf('.'));
        }
        return "test/"  + datePath + "/" + timePrefix + "-" + uuid + ext;
    }

    public void deleteObject(String objectName) {
        ossClient.deleteObject(aliOssProperties.getBucket(), objectName);
        log.debug("删除文件：{}", objectName);
    }

}

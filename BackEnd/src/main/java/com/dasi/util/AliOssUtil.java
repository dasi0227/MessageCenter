package com.dasi.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.dasi.common.properties.AliOssProperties;
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

    @Autowired
    private OSS ossClient;

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

    public String putObject(byte[] bytes, String objectName) {
        ossClient.putObject(aliOssProperties.getBucket(), objectName, new ByteArrayInputStream(bytes));
        String url = "https://" + aliOssProperties.getBucket() + "." + aliOssProperties.getEndpoint() + "/" + objectName;
        log.debug("文件上传到：{}", url);
        return url;
    }

    public void deleteObject(String objectName) {
        ossClient.deleteObject(aliOssProperties.getBucket(), objectName);
        log.debug("删除文件：{}", objectName);
    }

    public byte[] getObject(String objectName) {
        OSSObject ossObject = ossClient.getObject(aliOssProperties.getBucket(), objectName);
        return IoUtil.readBytes(ossObject.getObjectContent());
    }

}

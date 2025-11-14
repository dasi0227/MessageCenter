package com.dasi.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dasi.common.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@Slf4j
public class AliOssUtil {

    @Autowired
    private AliOssProperties aliOssProperties;

    public String upload(byte[] bytes, String objectName) {
        // 1. 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());

        // 2. 调用 putObject 方法上传
        ossClient.putObject(aliOssProperties.getBucket(), objectName, new ByteArrayInputStream(bytes));
        ossClient.shutdown();

        // 3. 拼接 URL：https://<bucketName>.<endpoint>/<objectName>
        String url = "https://" + aliOssProperties.getBucket() + "." + aliOssProperties.getEndpoint() + "/" + objectName;

        // 4. 返回文件存储的 URL
        log.debug("文件上传到：{}", url);
        return url;
    }

}

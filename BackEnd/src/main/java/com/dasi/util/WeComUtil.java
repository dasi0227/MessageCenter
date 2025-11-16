package com.dasi.util;

import com.dasi.common.constant.RedisConstant;
import com.dasi.common.exception.WeComException;
import com.dasi.common.properties.WeComProperties;
import com.dasi.pojo.wecom.WeComMediaResponse;
import com.dasi.pojo.wecom.WeComSendResponse;
import com.dasi.pojo.wecom.WeComTokenResponse;
import com.dasi.pojo.wecom.WeComUserIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class WeComUtil {

    @Autowired
    private WeComProperties weComProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WebClient webClient;

    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    private static final String MESSAGE_SEND_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    private static final String FILE_UPLOAD_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=file";

    private static final String USERID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=%s";

    public String getUserIdByPhone(String phone) {
        String accessToken = getAccessToken();
        String url = String.format(USERID_URL, accessToken);

        Map<String, String> body = Map.of("mobile", phone);

        WeComUserIdResponse response = webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(WeComUserIdResponse.class)
                .block();

        if (response == null || response.getErrcode() != 0) {
            String errorMsg = response != null ? response.getErrmsg() : "响应为空";
            log.error("【WeComUtil】获取 UserId 失败：{}", errorMsg);
            throw new WeComException(errorMsg);
        }

        String userId = response.getUserid();
        log.debug("【WeComUtil】获取 UserId 成功：{}", userId);
        return userId;
    }

    public String getAccessToken() {
        String accessToken = redisTemplate.opsForValue().get(RedisConstant.WECOME_ACCESS_TOKEN_KEY);

        if (accessToken != null) {
            return accessToken;
        }

        String url = String.format(ACCESS_TOKEN_URL, weComProperties.getCorpId(), weComProperties.getSecret());

        WeComTokenResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(WeComTokenResponse.class)
                .block();

        if (response == null || response.getErrcode() != 0) {
            String errorMsg = response != null ? response.getErrmsg() : "响应为空";
            log.error("【WeComUtil】获取 AccessToken 失败：{}", errorMsg);
            throw new WeComException(errorMsg);
        }

        accessToken = response.getAccess_token();
        int expire = Math.max(response.getExpires_in() - 300, 600);
        redisTemplate.opsForValue().set(RedisConstant.WECOME_ACCESS_TOKEN_KEY, accessToken, Duration.ofSeconds(expire));
        log.debug("【WeComUtil】获取 AccessToken 成功：{}", accessToken);
        return accessToken;
    }

    public void sendText(String userid, String content) {
        String accessToken = getAccessToken();
        String url = String.format(MESSAGE_SEND_URL, accessToken);

        Map<String, Object> body = Map.of(
                "touser", userid,
                "msgtype", "text",
                "agentid", weComProperties.getAgentId(),
                "text", Map.of("content", content),
                "safe", 0
        );

        WeComSendResponse response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(WeComSendResponse.class)
                .block();

        if (response == null || response.getErrcode() != 0) {
            String errorMsg = response != null ? response.getErrmsg() : "响应为空";
            log.error("【WeComUtil】发送消息失败：{}", errorMsg);
            throw new WeComException(errorMsg);
        }

        log.debug("【WeComUtil】发送消息成功：{}", response);
    }

    public String uploadFile(byte[] bytes, String fileName) {

        String accessToken = getAccessToken();
        String url = String.format(FILE_UPLOAD_URL, accessToken);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("media", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        WeComMediaResponse response = webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(WeComMediaResponse.class)
                .block();

        if (response == null || response.getErrcode() != 0) {
            throw new WeComException(
                    response != null ? response.getErrmsg() : "响应为空"
            );
        }

        return response.getMedia_id();
    }

    public void sendFile(String userIds, String mediaId) {
        String accessToken = getAccessToken();
        String url = String.format(MESSAGE_SEND_URL, accessToken);

        Map<String, Object> body = Map.of(
                "touser", userIds,
                "msgtype", "file",
                "agentid", weComProperties.getAgentId(),
                "file", Map.of("media_id", mediaId)
        );

        WeComSendResponse response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(WeComSendResponse.class)
                .block();

        if (response == null || response.getErrcode() != 0) {
            String errorMsg = response != null ? response.getErrmsg() : "响应为空";
            log.error("【WeComUtil】发送文件失败：{}", errorMsg);
            throw new WeComException(errorMsg);
        }

        log.debug("【WeComUtil】发送文件成功：{}", response);
    }

}

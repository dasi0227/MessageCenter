package com.dasi.util;

import cn.hutool.json.JSONUtil;
import com.dasi.common.properties.ModelProperties;
import com.dasi.common.properties.ModelProperties.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AiClientUtil {

    @Autowired
    private ModelProperties modelProperties;

    private final WebClient webClient = WebClient.builder().build();

    public String call(String model, String systemPrompt, String userPrompt) {
        ApiParam apiParam = modelProperties.getModels().get(model);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 2000,
                "temperature", 0.6
        );

        String response = webClient.post()
                .uri(apiParam.getUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiParam.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        String result = JSONUtil.parseObj(response).getByPath("choices[0].message.content", String.class);
        log.debug("【{} 调用】获取响应：{}", model, result);
        return result;
    }
}

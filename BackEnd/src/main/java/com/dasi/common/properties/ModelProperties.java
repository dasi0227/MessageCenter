package com.dasi.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dasi.llm")
@Data
public class ModelProperties {
    private Map<String, ApiParam> models;

    @Data
    public static class ApiParam {
        private String url;
        private String key;
    }

    public List<String> getModelList() {
        return new ArrayList<>(models.keySet());
    }
}

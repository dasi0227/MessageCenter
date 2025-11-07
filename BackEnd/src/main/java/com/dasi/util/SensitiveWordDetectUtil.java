package com.dasi.util;

import com.dasi.core.mapper.SensitiveWordMapper;
import com.dasi.pojo.entity.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SensitiveWordDetectUtil {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    private static final Set<String> SENSITIVE_WORDS = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void load() {
        SENSITIVE_WORDS.clear();
        SENSITIVE_WORDS.addAll(sensitiveWordMapper.selectAllWords());
    }

    public void reload() {
        load();
    }

    public Set<String> getWords() {
        return SENSITIVE_WORDS;
    }

    public String detect(Message message) {
        String text = (message.getSubject() + message.getContent()).toLowerCase();
        return SENSITIVE_WORDS.stream()
                .filter(text::contains)
                .collect(Collectors.joining(",")
        );
    }
}
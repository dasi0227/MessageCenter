package com.dasi.core.service;

import com.dasi.pojo.dto.PromptDTO;
import jakarta.validation.Valid;

public interface SystemService {
    void flush(String entity);

    String getLlmMessage(@Valid PromptDTO dto);
}

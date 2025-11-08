package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.dto.SensitiveWordUpdateDTO;
import com.dasi.pojo.dto.SensitiveWordsAddDTO;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.SensitiveWord;
import jakarta.validation.Valid;

import java.util.List;

public interface SensitiveWordService extends IService<SensitiveWord> {
    void addSensitiveWords(SensitiveWordsAddDTO dto);

    void removeSensitiveWord(Long id);

    void updateSensitiveWord(@Valid SensitiveWordUpdateDTO dto);

    List<SensitiveWord> getSensitiveWordList();

    void detect(Dispatch dispatch);
}

package com.dasi.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.SensitiveWordException;
import com.dasi.core.mapper.SensitiveWordMapper;
import com.dasi.core.service.SensitiveWordService;
import com.dasi.pojo.dto.SensitiveWordUpdateDTO;
import com.dasi.pojo.dto.SensitiveWordsAddDTO;
import com.dasi.pojo.entity.SensitiveWord;
import com.dasi.util.SensitiveWordDetectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Autowired
    private SensitiveWordDetectUtil sensitiveWordDetectUtil;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public List<SensitiveWord> getSensitiveWordList() {
        List<SensitiveWord> words = list(new LambdaQueryWrapper<SensitiveWord>().orderByDesc(SensitiveWord::getCreatedAt));
        log.debug("【SensitiveWord Service】查询敏感词列表：{}", words);
        return words;
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public void addSensitiveWords(SensitiveWordsAddDTO dto) {
        Set<String> input = dto.getWords();
        Set<String> exist = sensitiveWordDetectUtil.getWords();

        Set<String> newWords = input.stream()
                .filter(word -> !exist.contains(word))
                .collect(Collectors.toSet());

        if (newWords.isEmpty()) {
            throw new SensitiveWordException(ResultInfo.SENSITIVE_WORD_ALREADY_EXISTS);
        }

        List<SensitiveWord> words = newWords.stream()
                .map(word -> SensitiveWord.builder().createdAt(dto.getCreatedAt()).word(word).build())
                .toList();

        sensitiveWordMapper.insertWords(words);
        sensitiveWordDetectUtil.reload();

        log.debug("【SensitiveWord Service】新增敏感词：{}", newWords);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void removeSensitiveWord(String id) {
        if (!removeById(id)) {
            throw new SensitiveWordException(ResultInfo.SENSITIVE_WORD_REMOVE_FAIL);
        }
        log.debug("【SensitiveWord Service】删除敏感词：{}", id);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void updateSensitiveWord(SensitiveWordUpdateDTO dto) {
        if (exists(new LambdaQueryWrapper<SensitiveWord>().eq(SensitiveWord::getWord, dto.getWord()))) {
            throw new SensitiveWordException(ResultInfo.SENSITIVE_WORD_NOT_FOUND);
        }
        if (!update(new LambdaUpdateWrapper<SensitiveWord>()
                .eq(SensitiveWord::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getWord()), SensitiveWord::getWord, dto.getWord()))) {
            throw new SensitiveWordException(ResultInfo.SENSITIVE_WORD_UPDATE_FAIL);
        }
        sensitiveWordDetectUtil.reload();
        log.debug("【SensitiveWord Service】更新敏感词：{}", dto);
    }
}

package com.dasi.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.exception.SendException;
import com.dasi.core.mapper.SensitiveWordMapper;
import com.dasi.core.service.SensitiveWordService;
import com.dasi.pojo.dto.SensitiveWordUpdateDTO;
import com.dasi.pojo.dto.SensitiveWordsAddDTO;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.SensitiveWord;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Override
    public List<SensitiveWord> getSensitiveWordList() {
        return list(new LambdaQueryWrapper<SensitiveWord>().orderByDesc(SensitiveWord::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.INSERT)
    public void addSensitiveWords(SensitiveWordsAddDTO dto) {
        // 得到差集
        Set<String> keep = new HashSet<>(dto.getWords());
        keep.removeAll(WORD_CACHE);

        if (keep.isEmpty()) {
            log.warn("【SensitiveWord Service】插入失败，全部敏感词都已经存在：{}", dto);
        } else {
            List<SensitiveWord> words = keep.stream()
                    .map(word -> SensitiveWord.builder()
                            .word(word)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
                    .toList();
            saveBatch(words);
            reload();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(fieldName = "word")
    public void updateSensitiveWord(SensitiveWordUpdateDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<SensitiveWord>()
                .eq(SensitiveWord::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getWord()), SensitiveWord::getWord, dto.getWord())
                .set(SensitiveWord::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【SensitiveWord Service】更新失败，没有记录或值无变化：{}", dto);
        } else {
            reload();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    public void removeSensitiveWord(Long id) {
        boolean flag = removeById(id);
        if (!flag) {
            log.warn("【SensitiveWord Service】删除失败，敏感词不存在：{}", id);
        } else {
            reload();
        }
    }

    private static final Set<String> WORD_CACHE = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        reload();
    }

    public synchronized void reload() {
        WORD_CACHE.clear();
        list().forEach(word -> {
            if (StrUtil.isNotBlank(word.getWord())) {
                WORD_CACHE.add(word.getWord());
            }
        });
    }

    @Override
    public void detect(Dispatch dispatch) {
        String text = StrUtil.emptyToDefault(dispatch.getSubject(), "")
                + StrUtil.emptyToDefault(dispatch.getContent(), "");

        Set<String> hitWords = new LinkedHashSet<>();
        for (String word : WORD_CACHE) {
            if (Pattern.compile(Pattern.quote(word)).matcher(text).find()) {
                hitWords.add(word);
            }
        }

        if (!hitWords.isEmpty()) {
            String errorMsg = SendConstant.SENSITIVE_HIT + hitWords;
            log.warn("【敏感词检查】{}", errorMsg);
            throw new SendException(errorMsg);
        }
    }
}
package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.enumeration.FillType;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Autowired
    private SensitiveWordDetectUtil sensitiveWordDetectUtil;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

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
        keep.removeAll(sensitiveWordDetectUtil.getWords());

        if (keep.isEmpty()) {
            log.warn("【SensitiveWord Service】插入失败，全部敏感词都已经存在：{}", dto);
        } else {
            List<SensitiveWord> words = keep.stream()
                    .map(word -> BeanUtil.copyProperties(dto, SensitiveWord.class))
                    .toList();
            sensitiveWordMapper.insertWords(words);
            sensitiveWordDetectUtil.reload();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(serviceClass = SensitiveWordServiceImpl.class, fieldName = "word")
    public void updateSensitiveWord(SensitiveWordUpdateDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<SensitiveWord>()
                .eq(SensitiveWord::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getWord()), SensitiveWord::getWord, dto.getWord())
                .set(SensitiveWord::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【SensitiveWord Service】更新失败，没有记录或值无变化：{}", dto);
        } else {
            sensitiveWordDetectUtil.reload();
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
            sensitiveWordDetectUtil.reload();
        }
    }
}
package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.enumeration.FillType;
import com.dasi.core.mapper.TemplateMapper;
import com.dasi.core.service.TemplateService;
import com.dasi.pojo.dto.TemplateAddDTO;
import com.dasi.pojo.dto.TemplateUpdateDTO;
import com.dasi.pojo.entity.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Override
    @Cacheable(value = RedisConstant.CACHE_TEMPLATE_PREFIX, key = "'list'")
    public List<Template> getTemplateList() {
        return list(new LambdaQueryWrapper<Template>().orderByDesc(Template::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @UniqueField(fieldName = "name")
    @CacheEvict(value = RedisConstant.CACHE_TEMPLATE_PREFIX, allEntries = true)
    public void addTemplate(TemplateAddDTO dto) {
        Template template = BeanUtil.copyProperties(dto, Template.class);
        boolean flag = save(template);

        if (flag) {
            log.warn("【Template Service】插入失败：{}", dto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(fieldName = "name")
    @CacheEvict(value = RedisConstant.CACHE_TEMPLATE_PREFIX, allEntries = true)
    public void updateTemplate(TemplateUpdateDTO dto) {
        boolean flag = update(new LambdaUpdateWrapper<Template>()
                .eq(Template::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Template::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getSubject()), Template::getSubject, dto.getSubject())
                .set(StrUtil.isNotBlank(dto.getContent()), Template::getContent, dto.getContent())
                .set(Template::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Template Service】更新失败，没有记录或者值无变化：{}", dto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @CacheEvict(value = RedisConstant.CACHE_TEMPLATE_PREFIX, allEntries = true)
    public void removeTemplate(Long id) {
        boolean flag = removeById(id);

        if (!flag) {
            log.warn("【Template Service】删除失败，模板不存在：{}", id);
        }
    }
}

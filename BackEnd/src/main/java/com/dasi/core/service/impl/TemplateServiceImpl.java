package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.TemplateException;
import com.dasi.core.mapper.TemplateMapper;
import com.dasi.core.service.TemplateService;
import com.dasi.pojo.dto.TemplateAddDTO;
import com.dasi.pojo.dto.TemplateUpdateDTO;
import com.dasi.pojo.entity.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Override
    public List<Template> getTemplateList() {
        List<Template> list = list(new LambdaQueryWrapper<Template>().orderByDesc(Template::getCreatedAt));
        log.debug("【Template Service】获取模板列表：{}", list);
        return list;
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(TemplateAddDTO dto) {
        if (exists(new LambdaQueryWrapper<Template>().eq(Template::getName, dto.getName()))) {
            throw new TemplateException(ResultInfo.TEMPLATE_ALREADY_EXISTS);
        }

        Template template = BeanUtil.copyProperties(dto, Template.class);
        save(template);

        log.debug("【Template Service】添加模板成功：{}", template);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplate(Long id) {
        if (!removeById(id)) {
            throw new TemplateException(ResultInfo.TEMPLATE_REMOVE_FAIL);
        }
        log.debug("【Template Service】删除模板：{}", id);
    }

    @Override
    @AutoFill(FillType.UPDATE)
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(TemplateUpdateDTO dto) {
        if (!exists(new LambdaQueryWrapper<Template>().eq(Template::getId, dto.getId()))) {
            throw new TemplateException(ResultInfo.TEMPLATE_NOT_FOUND);
        }
        if (!update(new LambdaUpdateWrapper<Template>()
                .eq(Template::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Template::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getSubject()), Template::getSubject, dto.getSubject())
                .set(StrUtil.isNotBlank(dto.getContent()), Template::getContent, dto.getContent())
                .set(Template::getUpdatedAt, dto.getUpdatedAt()))) {
            throw new TemplateException(ResultInfo.TEMPLATE_UPDATE_FAIL);
        }

        log.debug("【Template Service】更新模板：{}", dto);
    }
}

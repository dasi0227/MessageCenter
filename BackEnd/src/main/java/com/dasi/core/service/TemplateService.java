package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.dto.TemplateAddDTO;
import com.dasi.pojo.dto.TemplateUpdateDTO;
import com.dasi.pojo.entity.Template;
import jakarta.validation.Valid;

import java.util.List;

public interface TemplateService extends IService<Template> {
    List<Template> getTemplateList();

    void addTemplate(@Valid TemplateAddDTO dto);

    void removeTemplate(Long id);

    void updateTemplate(@Valid TemplateUpdateDTO dto);
}

package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Render;
import com.dasi.pojo.dto.RenderAddDTO;
import com.dasi.pojo.dto.RenderUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface RenderService extends IService<Render> {
    List<Render> getRenderList();

    void addRender(@Valid RenderAddDTO dto);

    void updateRender(@Valid RenderUpdateDTO dto);

    void removeRender(Long id);

    void decode(Dispatch dispatch);
}

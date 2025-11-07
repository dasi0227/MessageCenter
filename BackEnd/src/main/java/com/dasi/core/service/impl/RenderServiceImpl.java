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
import com.dasi.common.exception.RenderException;
import com.dasi.core.mapper.RenderMapper;
import com.dasi.core.service.RenderService;
import com.dasi.pojo.entity.Render;
import com.dasi.pojo.dto.RenderAddDTO;
import com.dasi.pojo.dto.RenderUpdateDTO;
import com.dasi.util.RenderResolveUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.dasi.common.constant.DefaultConstant.DEFAULT_RENDER_IDS;

@Service
@Slf4j
public class RenderServiceImpl extends ServiceImpl<RenderMapper, Render> implements RenderService {

    @Autowired
    private RenderResolveUtil renderResolveUtil;

    @Override
    public List<Render> getRenderList() {
        List<Render> list = list(new LambdaQueryWrapper<Render>().orderByAsc(Render::getCreatedAt));
        log.debug("【Render Service】查询渲染字段列表：{}", list);
        return list;
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public void addRender(RenderAddDTO dto) {
        if (exists(new LambdaQueryWrapper<Render>().eq(Render::getKey, dto.getKey()))) {
            throw new RenderException(ResultInfo.RENDER_KEY_ALREADY_EXISTS);
        }

        Render render = BeanUtil.copyProperties(dto, Render.class);
        save(render);
        renderResolveUtil.reload();
        log.debug("【Render Service】新增渲染字段：{}", render);
    }

    @Override
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public void updateRender(RenderUpdateDTO dto) {
        if (DEFAULT_RENDER_IDS.contains(dto.getId())) {
            throw new RenderException(ResultInfo.RENDER_UPDATE_FAIL);
        }
        if (exists(new LambdaQueryWrapper<Render>().
                eq(Render::getKey, dto.getKey())
                .ne(Render::getId, dto.getId()))) {
            throw new RenderException(ResultInfo.RENDER_KEY_ALREADY_EXISTS);
        }
        if (!update(new LambdaUpdateWrapper<Render>()
                .eq(Render::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getKey()), Render::getKey, dto.getKey())
                .set(StrUtil.isNotBlank(dto.getValue()), Render::getValue, dto.getValue())
                .set(StrUtil.isNotBlank(dto.getRemark()), Render::getRemark, dto.getRemark())
                .set(Render::getUpdatedAt, LocalDateTime.now()))) {
            throw new RenderException(ResultInfo.RENDER_UPDATE_FAIL);
        }
        renderResolveUtil.reload();
        log.debug("【Render Service】更新渲染字段：{}", dto);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public void removeRender(Long id) {
        if (DEFAULT_RENDER_IDS.contains(id)) {
            throw new RenderException(ResultInfo.RENDER_REMOVE_FAIL);
        }
        if (!removeById(id)) {
            throw new RenderException(ResultInfo.RENDER_REMOVE_FAIL);
        }
        renderResolveUtil.reload();
        log.debug("【Render Service】删除渲染字段：{}", id);
    }
}

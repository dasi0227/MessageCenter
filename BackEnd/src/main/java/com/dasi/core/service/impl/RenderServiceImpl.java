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
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
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

import java.util.List;

import static com.dasi.common.constant.DefaultConstant.DEFAULT_RENDER_IDS;

@Service
@Slf4j
public class RenderServiceImpl extends ServiceImpl<RenderMapper, Render> implements RenderService {

    @Autowired
    private RenderResolveUtil renderResolveUtil;

    @Override
    public List<Render> getRenderList() {
        return list(new LambdaQueryWrapper<Render>().orderByAsc(Render::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @UniqueField(serviceClass = RenderServiceImpl.class, fieldName = "key")
    public void addRender(RenderAddDTO dto) {
        Render render = BeanUtil.copyProperties(dto, Render.class);
        boolean flag = save(render);

        if (!flag) {
            log.warn("【Render Service】插入失败：{}", dto);
        } else {
            renderResolveUtil.reload();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(serviceClass = RenderServiceImpl.class, fieldName = "key")
    public void updateRender(RenderUpdateDTO dto) {
        if (DEFAULT_RENDER_IDS.contains(dto.getId())) {
            log.warn("【Render Service】更新失败，系统预设字段不可修改：{}", dto);
            throw new MessageCenterException(ResultInfo.RENDER_UPDATE_FAIL);
        }

        boolean flag = update(new LambdaUpdateWrapper<Render>()
                .eq(Render::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getKey()), Render::getKey, dto.getKey())
                .set(StrUtil.isNotBlank(dto.getValue()), Render::getValue, dto.getValue())
                .set(StrUtil.isNotBlank(dto.getRemark()), Render::getRemark, dto.getRemark())
                .set(Render::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Render Service】更新失败，没有记录或值无变化：{}", dto);
        } else {
            renderResolveUtil.reload();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    public void removeRender(Long id) {
        if (DEFAULT_RENDER_IDS.contains(id)) {
            log.warn("【Render Service】删除失败，系统预设字段不可删除：{}", id);
            throw new RenderException(ResultInfo.RENDER_REMOVE_FAIL);
        }

        boolean flag = removeById(id);
        if (!flag) {
            log.warn("【Render Service】删除失败，渲染字段不存在：{}", id);
        } else {
            renderResolveUtil.reload();
        }
    }
}

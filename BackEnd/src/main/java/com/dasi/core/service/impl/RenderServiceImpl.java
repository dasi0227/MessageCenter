package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AdminOnly;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.annotation.UniqueField;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.common.exception.RenderException;
import com.dasi.common.exception.SendException;
import com.dasi.core.mapper.RenderMapper;
import com.dasi.core.service.RenderService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Render;
import com.dasi.pojo.dto.RenderAddDTO;
import com.dasi.pojo.dto.RenderUpdateDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class RenderServiceImpl extends ServiceImpl<RenderMapper, Render> implements RenderService {

    @Override
    public List<Render> getRenderList() {
        return list(new LambdaQueryWrapper<Render>().orderByAsc(Render::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.INSERT)
    @UniqueField(fieldName = "name")
    public void addRender(RenderAddDTO dto) {
        Render render = BeanUtil.copyProperties(dto, Render.class);
        boolean flag = save(render);

        if (!flag) {
            log.warn("【Render Service】插入失败：{}", dto);
        } else {
            reload();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    @AutoFill(FillType.UPDATE)
    @UniqueField(fieldName = "name")
    public void updateRender(RenderUpdateDTO dto) {
        if (SYS_KEYS_ID.contains(dto.getId())) {
            log.warn("【Render Service】更新失败，系统预设字段不可修改：{}", dto);
            throw new MessageCenterException(ResultInfo.RENDER_UPDATE_FAIL);
        }

        boolean flag = update(new LambdaUpdateWrapper<Render>()
                .eq(Render::getId, dto.getId())
                .set(StrUtil.isNotBlank(dto.getName()), Render::getName, dto.getName())
                .set(StrUtil.isNotBlank(dto.getValue()), Render::getValue, dto.getValue())
                .set(StrUtil.isNotBlank(dto.getRemark()), Render::getRemark, dto.getRemark())
                .set(Render::getUpdatedAt, dto.getUpdatedAt()));

        if (!flag) {
            log.warn("【Render Service】更新失败，没有记录或值无变化：{}", dto);
        } else {
            reload();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AdminOnly
    public void removeRender(Long id) {
        if (SYS_KEYS_ID.contains(id)) {
            log.warn("【Render Service】删除失败，系统预设字段不可删除：{}", id);
            throw new RenderException(ResultInfo.RENDER_REMOVE_FAIL);
        }

        boolean flag = removeById(id);
        if (!flag) {
            log.warn("【Render Service】删除失败，渲染字段不存在：{}", id);
        } else {
            reload();
        }
    }

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Map<String, String> RENDER_MAP = new ConcurrentHashMap<>();
    private static final Set<String> SYS_KEYS_NAME = Set.of("#account", "#contact", "#department", "#date", "#datetime", "#uuid");
    private static final Set<Long> SYS_KEYS_ID = Set.of(1L, 2L, 3L, 4L, 5L);

    @PostConstruct
    public void init() {
        reload();
    }

    public synchronized void reload() {
        RENDER_MAP.clear();
        list().stream()
                .filter(r -> r.getName() != null && !r.getName().isEmpty())
                .filter(r -> r.getValue() != null)
                .forEach(r -> RENDER_MAP.put(r.getName(), r.getValue()));
    }

    @Override
    public void decode(Dispatch dispatch) {
        Set<String> unmatched = new LinkedHashSet<>();

        String subject = renderText(dispatch.getSubject(), dispatch, unmatched);
        String content = renderText(dispatch.getContent(), dispatch, unmatched);
        dispatch.setSubject(subject);
        dispatch.setContent(content);

        if (!unmatched.isEmpty()) {
            String errorMsg = SendConstant.RENDER_UNMATCHED + unmatched;
            log.warn("【渲染检查】{}", errorMsg);
            throw new SendException(errorMsg);
        }
    }

    private String renderText(String text, Dispatch dispatch, Set<String> unmatchedKeys) {
        if (StrUtil.isBlank(text)) return text;

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = resolveValue(key, dispatch);

            if (value == null) {
                unmatchedKeys.add(key);
                value = Matcher.quoteReplacement("${" + key + "}");
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private String resolveValue(String key, Dispatch dispatch) {
        // 系统变量
        if (SYS_KEYS_NAME.contains(key)) {
            return switch (key) {
                case "#contact" -> dispatch.getContactName();
                case "#department" -> dispatch.getDepartmentName();
                case "#account" -> AccountContextHolder.get().getName();
                case "#uuid"    -> UUID.randomUUID().toString();
                case "#date" -> LocalDate.now().toString();
                case "#datetime" -> LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                default -> null;
            };
        }

        // 数据库变量
        if (RENDER_MAP.containsKey(key)) {
            return RENDER_MAP.get(key);
        }

        return null;
    }
}

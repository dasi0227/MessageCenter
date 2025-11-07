package com.dasi.util;

import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.RenderException;
import com.dasi.core.mapper.AccountMapper;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.DepartmentMapper;
import com.dasi.core.mapper.RenderMapper;
import com.dasi.pojo.entity.Account;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Department;
import com.dasi.pojo.entity.Dispatch;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RenderResolveUtil {

    @Autowired
    private RenderMapper renderMapper;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    private static final Map<String, String> RENDER_MAP = new ConcurrentHashMap<>();
    private static final Pattern PLACEHOLDER = Pattern.compile("¥\\{([^}]+)}¥");
    private static final Set<String> SYS_KEYS = Set.of("#contact", "#department", "#account", "#date", "#datetime");

    @PostConstruct
    public void load() {
        RENDER_MAP.clear();
        renderMapper.selectList(null).stream()
                .filter(r -> r.getKey() != null && !r.getKey().isEmpty())
                .filter(r -> r.getValue() != null)
                .forEach(r -> RENDER_MAP.put(r.getKey(), r.getValue()));
    }

    public void reload() {
        load();
    }

    public String resolve(String text, Dispatch dispatch) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 收集所有占位符
        Matcher matcher = PLACEHOLDER.matcher(text);
        List<String> keys = new ArrayList<>();
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }

        // 构造系统变量映射
        Contact contact = contactMapper.selectById(dispatch.getContactId());
        Department department = departmentMapper.selectById(dispatch.getDepartmentId());
        Account account = accountMapper.selectById(dispatch.getAccountId());

        if (contact == null || department == null || account == null) {
            throw new RenderException(ResultInfo.RENDER_SYS_VALUE_MISSING);
        }

        Map<String, String> sysVars = Map.of(
                "#contact", contact.getName(),
                "#department", department.getName(),
                "#account", account.getName(),
                "#date", LocalDate.now().toString(),
                "#datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        String result = text;

        for (String key : keys) {
            String placeholder = "¥{" + key + "}¥";
            String value;

            if (SYS_KEYS.contains(key)) {
                value = sysVars.get(key);
                if (value == null) {
                    throw new RenderException(ResultInfo.RENDER_SYS_VALUE_MISSING);
                }
            } else {
                value = RENDER_MAP.get(key);
                if (value == null) {
                    throw new RenderException(ResultInfo.RENDER_KEY_NOT_FOUND);
                }
            }

            result = result.replace(placeholder, value);
        }

        // 校验是否仍有未解析的占位符
        if (PLACEHOLDER.matcher(result).find()) {
            throw new RenderException(ResultInfo.RENDER_PLACEHOLDER_UNRESOLVED);
        }

        log.debug("【RenderResolveUtil】渲染结果：{}", result);
        return result;
    }
}
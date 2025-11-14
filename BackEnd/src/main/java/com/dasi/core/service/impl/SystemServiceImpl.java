package com.dasi.core.service.impl;

import com.dasi.common.constant.RedisConstant;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.core.service.SystemService;
import com.dasi.pojo.dto.PromptDTO;
import com.dasi.util.AiClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AiClientUtil aiClientUtil;

    @Override
    public void flush(String entity) {
        String cacheName = switch (entity.toLowerCase()) {
            case "account"      -> RedisConstant.CACHE_ACCOUNT_PREFIX;
            case "contact"      -> RedisConstant.CACHE_CONTACT_PREFIX;
            case "department"   -> RedisConstant.CACHE_DEPARTMENT_PREFIX;
            case "message"      -> RedisConstant.CACHE_MESSAGE_PREFIX;
            case "dispatch"     -> RedisConstant.CACHE_DISPATCH_PREFIX;
            case "template"     -> RedisConstant.CACHE_TEMPLATE_PREFIX;
            case "render"       -> RedisConstant.CACHE_RENDER_PREFIX;
            case "sensitive"    -> RedisConstant.CACHE_SENSITIVE_WORD_PREFIX;
            case "dashboard"    -> RedisConstant.CACHE_DASHBOARD_PREFIX;
            case "failure"      -> RedisConstant.CACHE_FAILURE_PREFIX;
            case "mailbox"      -> RedisConstant.CACHE_MAILBOX_PREFIX;
            default             -> null;
        };

        if (cacheName == null) {
            log.error("【System Service】未识别的实体名称：{}", entity);
            throw new MessageCenterException(ResultInfo.PATH_VALIDATE_FAIL);
        }

        Cache cache = redisCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("【System Service】已清空缓存模块：{}", cacheName);
        } else {
            Set<String> keys = redisTemplate.keys(cacheName + "*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("【System Service】已手动清除 Redis Key：{}", cacheName);
            } else {
                log.warn("【System Service】未找到缓存模块或 Redis key：{}", cacheName);
            }
        }
    }

    @Override
    public String getLlmMessage(PromptDTO dto) {

        String systemPrompt = """
            你是一名企业级「消息内容生成助手」，服务对象为内部消息中台（MessageCenter）系统。
            你的任务是根据用户提供的业务需求生成可直接发送的企业消息文本。

            【输出规则】
            1. 不输出标题，直接从正文开始。
            2. 内容必须正式、清晰、简洁，符合企业沟通规范。
            3. 不使用 Emoji、不使用口语化表达、不使用 Markdown、不使用代码块、不使用 JSON。
            4. 不出现任何 AI 身份描述、道歉语句、推测性内容或系统信息。
            5. 内容应结构合理，有自然段落。
            6. 若用户提供时间、地点、事件、要求等信息，你必须补全为逻辑完整的企业文本。
            7. 若用户输入模糊，你应基于企业场景进行合理补全，但不能编造无关信息。
            8. 输出必须是纯文本，适合直接存入数据库并投递给联系人。

            【场景增强要求】
            - 流程通知需包含步骤说明。
            - 异常/告警需语气严谨并包含行动要求。
            - 日常消息自然礼貌。
            - 附件如存在，可自然说明“相关资料已随邮件附上”。
            """;

        String userPrompt = """
            请根据以下用户需求生成一条可直接发送的消息内容：

            【用户需求】
            %s

            请直接给出最终内容。
            """.formatted(dto.getPrompt());

        return aiClientUtil.call(dto.getModel(), systemPrompt, userPrompt);
    }

}

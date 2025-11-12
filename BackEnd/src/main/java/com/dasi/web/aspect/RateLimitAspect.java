package com.dasi.web.aspect;

import com.dasi.common.annotation.RateLimit;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.exception.RateLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT;

    static {
        RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
        RATE_LIMIT_SCRIPT.setScriptText(RedisConstant.RATE_LIMIT_SCRIPT_TEXT);
    }

    @Before("@annotation(rateLimit)")
    public void rateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        String fullName = joinPoint.getSignature().getDeclaringType().getSimpleName() + "-" + joinPoint.getSignature().getName();

        String dynamicKey = "";
        if (!rateLimit.key().isEmpty()) {
            dynamicKey = ":" + parseSpEL(rateLimit.key(), joinPoint);
        }

        String key = RedisConstant.RATE_LIMIT_PREFIX + fullName + dynamicKey;
        boolean access = tryAccess(key, rateLimit.limit(), rateLimit.ttl());
        if (!access) {
            throw new RateLimitException(rateLimit.message());
        }
    }

    private String parseSpEL(String spel, JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return parser.parseExpression(spel).getValue(context, String.class);
    }

    public boolean tryAccess(String key, int limit, int ttl) {
        long result = redisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(ttl)
        );
        return result == 1L;
    }
}

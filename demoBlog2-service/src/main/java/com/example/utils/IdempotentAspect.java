package com.example.utils;


import com.example.api.dto.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        String key = generateKey(joinPoint, idempotent.key());

        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", idempotent.expireTime(), TimeUnit.MILLISECONDS);
        //1000000L
        //Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", idempotent.expireTime(), 1000000L);

        if (Boolean.TRUE.equals(isAbsent)) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                redisTemplate.delete(key);
                log.error("业务执行失败，删除幂等key: {}", key, e);
                throw e;
            }
        } else {
            log.warn("重复请求被拦截，key: {}, 路径: {}", key, request.getRequestURI());
            return Result.error(idempotent.message());
        }
    }

    private String generateKey(ProceedingJoinPoint joinPoint, String prefix) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = "";
        if (attributes != null) {
            requestId = attributes.getRequest().getHeader("X-Request-ID");
            if (requestId == null || requestId.isEmpty()) {
                requestId = attributes.getRequest().getRemoteAddr();
            }
        }

        return String.format("%s:%s:%s:%s", prefix, className, methodName, requestId);
    }
}

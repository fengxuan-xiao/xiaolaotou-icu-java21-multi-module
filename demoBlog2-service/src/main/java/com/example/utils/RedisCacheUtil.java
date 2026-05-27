package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisCacheUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ARTICLE_CACHE_KEY = "article:";
    private static final String USER_CACHE_KEY = "user:";
    private static final String PERMISSION_CACHE_KEY = "permission:";
    private static final String TODO_CACHE_KEY = "todo:";

    public void cacheArticle(Long articleId, Object articleData, long timeout, TimeUnit unit) {
        String key = ARTICLE_CACHE_KEY + articleId;
        try {
            redisTemplate.opsForValue().set(key, articleData, timeout, unit);
            log.debug("文章缓存成功，key: {}", key);
        } catch (Exception e) {
            log.error("文章缓存失败，key: {}", key, e);
        }
    }

    public Object getCachedArticle(Long articleId) {
        String key = ARTICLE_CACHE_KEY + articleId;
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (data != null) {
                log.debug("文章缓存命中，key: {}", key);
            }
            return data;
        } catch (Exception e) {
            log.error("获取文章缓存失败，key: {}", key, e);
            return null;
        }
    }

    public void evictArticleCache(Long articleId) {
        String key = ARTICLE_CACHE_KEY + articleId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("文章缓存删除{}，key: {}", deleted ? "成功" : "失败", key);
        } catch (Exception e) {
            log.error("删除文章缓存失败，key: {}", key, e);
        }
    }

    public void cacheUserPermissions(Long userId, Object permissionsData, long timeout, TimeUnit unit) {
        String key = PERMISSION_CACHE_KEY + userId;
        try {
            redisTemplate.opsForValue().set(key, permissionsData, timeout, unit);
            log.debug("用户权限缓存成功，key: {}", key);
        } catch (Exception e) {
            log.error("用户权限缓存失败，key: {}", key, e);
        }
    }

    public Object getCachedUserPermissions(Long userId) {
        String key = PERMISSION_CACHE_KEY + userId;
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (data != null) {
                log.debug("用户权限缓存命中，key: {}", key);
            }
            return data;
        } catch (Exception e) {
            log.error("获取用户权限缓存失败，key: {}", key, e);
            return null;
        }
    }

    public void evictUserPermissionsCache(Long userId) {
        String key = PERMISSION_CACHE_KEY + userId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("用户权限缓存删除{}，key: {}", deleted ? "成功" : "失败", key);
        } catch (Exception e) {
            log.error("删除用户权限缓存失败，key: {}", key, e);
        }
    }

    public void evictUserPermissions(Long userId) {
        evictUserPermissionsCache(userId);
    }

    public void cacheTodoList(Long userId, Object todoData, long timeout, TimeUnit unit) {
        String key = TODO_CACHE_KEY + userId;
        try {
            redisTemplate.opsForValue().set(key, todoData, timeout, unit);
            log.debug("待办列表缓存成功，key: {}", key);
        } catch (Exception e) {
            log.error("待办列表缓存失败，key: {}", key, e);
        }
    }

    public Object getCachedTodoList(Long userId) {
        String key = TODO_CACHE_KEY + userId;
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (data != null) {
                log.debug("待办列表缓存命中，key: {}", key);
            }
            return data;
        } catch (Exception e) {
            log.error("获取待办列表缓存失败，key: {}", key, e);
            return null;
        }
    }

    public void evictTodoListCache(Long userId) {
        String key = TODO_CACHE_KEY + userId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("待办列表缓存删除{}，key: {}", deleted ? "成功" : "失败", key);
        } catch (Exception e) {
            log.error("删除待办列表缓存失败，key: {}", key, e);
        }
    }

    public void invalidateAllUserCaches(Long userId) {
        evictArticleCache(userId);
        evictUserPermissionsCache(userId);
        evictTodoListCache(userId);
        log.info("用户所有缓存已清除，userId: {}", userId);
    }
}

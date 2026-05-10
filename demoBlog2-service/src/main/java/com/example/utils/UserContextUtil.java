package com.example.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserContextUtil {

    /**
     * 从当前请求中获取用户ID
     *
     * @return 用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            Object userIdObj = request.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);

            if (userIdObj == null) {
                return null;
            }

            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                return Long.parseLong((String) userIdObj);
            } else if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从当前请求中获取用户ID（字符串形式）
     *
     * @return 用户ID字符串，如果未登录则返回null
     */
    public static String getCurrentUserIdStr() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            Object userIdObj = request.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);

            if (userIdObj == null) {
                return null;
            }

            return userIdObj.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 如果已登录返回true，否则返回false
     */
    public static boolean isLoggedIn() {
        return getCurrentUserId() != null;
    }

    /**
     * 获取当前用户ID，如果未登录则抛出异常
     *
     * @return 用户ID
     * @throws RuntimeException 如果用户未登录
     */
    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }
}

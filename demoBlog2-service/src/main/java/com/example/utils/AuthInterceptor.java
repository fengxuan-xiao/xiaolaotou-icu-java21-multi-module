package com.example.utils;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    public static final String USER_ID_ATTRIBUTE = "userId";
    public static final String USER_NAME_ATTRIBUTE = "userName";
    /**
     * 请求预处理拦截器，验证用户身份
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 被调用的处理器对象
     * @return 如果token有效返回true放行请求，否则返回false并返回401错误
     * @throws Exception 处理过程中的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 Token
        String token = request.getHeader("Authorization");

        logger.warn("=== Token验证调试信息 ===");
        logger.warn("请求路径: {} {}", request.getMethod(), request.getRequestURI());
        logger.warn("Authorization头: {}", token);


        // 2. 判断 Token 是否存在且格式正确 (Bearer <token>)
        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("Token格式错误：缺少Bearer前缀或为空");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或Token无效\"}");
            return false; // 拦截请求
        }

        // 3. 验证 Token 有效性 (这里假设你有一个 JwtUtil 工具类)
        String realToken = token.substring(7);

        logger.warn("提取的Token内容: {}", realToken);
        logger.warn("Token是否为[object Object]: {}", "[object Object]".equals(realToken));

        if (realToken.isEmpty()) {
            logger.error("Token内容为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token为空\"}");
            return false;
        }

        if ("[object Object]".equals(realToken)) {
            logger.error("========================================");
            logger.error("前端传递的Token是 [object Object]！");
            logger.error("这说明前端把一个对象转成了字符串，而不是实际的token值");
            logger.error("请检查前端代码：");
            logger.error("1. 登录时是否正确提取了token字段（如 res.data.token）");
            logger.error("2. 存储时是否存储的是字符串");
            logger.error("3. 请求时是否正确获取了token字符串");
            logger.error("========================================");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token格式错误：前端传递了对象而非字符串，请检查前端代码\"}");
            return false;
        }

        try {
            // URL 解码（处理前端传输时的 URL 编码问题）
            //realToken = URLDecoder.decode(realToken, StandardCharsets.UTF_8);

            String decodedToken = realToken;
            try {
                // URL 解码（处理前端传输时的 URL 编码问题）
                decodedToken = URLDecoder.decode(realToken, StandardCharsets.UTF_8);
                logger.debug("URL解码后的Token前20字符: {}",
                        decodedToken.length() > 20 ? decodedToken.substring(0, 20) + "..." : decodedToken);
            } catch (Exception decodeEx) {
                logger.debug("URL解码失败，使用原始Token: {}", decodeEx.getMessage());
                decodedToken = realToken;
            }


            // 如果解析失败或过期，会抛出异常
            JwtUtil.verifyToken(realToken);
            logger.info("Token验证成功");
            // 可选：将用户信息存入 request，供 Controller 使用
            // request.setAttribute("userId", JwtUtil.getUserId(realToken));
            String userId = JwtUtil.getUserIdFromToken(realToken);
            if (userId != null) {
                request.setAttribute(USER_ID_ATTRIBUTE, userId);
                request.setAttribute(USER_NAME_ATTRIBUTE, JwtUtil.getUserNameFromToken(realToken));
                //request.setAttribute(USER_NAME_ATTRIBUTE, JwtUtil.getUserNameFromToken(realToken));
                logger.debug("用户ID已存入request: {}", userId);
            }
            return true; // 放行
        } catch (Exception e) {
            logger.error("Token验证失败: {}", e.getMessage());
            logger.debug("Token异常详情", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token已过期或无效\"}");
            return false;
        }
    }
}

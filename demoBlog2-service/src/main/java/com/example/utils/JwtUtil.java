package com.example.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY_STRING = "my_secret_key_for_blog_system_very_long_string";

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成JWT访问令牌
     *
     * @param userId 用户唯一标识，将作为token的主题和自定义声明
     * @return 生成的JWT token字符串，包含用户ID、签发时间、过期时间等信息，使用HS256算法签名
     */
    /*public static String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }*/
    public static String generateToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证JWT令牌的有效性并解析其内容
     *
     * @param token 待验证的JWT token字符串
     * @return Claims对象，包含token中的所有声明信息（如userId、签发时间、过期时间等）
     * @throws Exception 当token无效、格式错误或已过期时抛出异常
     */
    public static Claims verifyToken(String token) throws Exception {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new Exception("Invalid or expired token");
        }
    }

    /**
     * 从JWT令牌中提取用户ID
     *
     * @param token JWT token字符串
     * @return 用户ID字符串，如果token无效或过期则返回null
     */
    public static String getUserIdFromToken(String token) {
        try {
            Claims claims = verifyToken(token);
            return claims.get("userId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserNameFromToken(String token) {
        try {
            Claims claims = verifyToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查JWT令牌是否有效
     *
     * @param token JWT token字符串
     * @return 如果token有效返回true，否则返回false
     */
    public static boolean isTokenValid(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
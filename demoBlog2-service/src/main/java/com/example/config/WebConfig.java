package com.example.config;


import com.example.utils.AuthInterceptor;
import com.example.utils.RequestIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/articles/**") // 保护文章接口
                .addPathPatterns("/user/info")   // 保护用户信息接口
                .excludePathPatterns("/user/login", "/user/register"); // 排除登录注册接口
    }
}

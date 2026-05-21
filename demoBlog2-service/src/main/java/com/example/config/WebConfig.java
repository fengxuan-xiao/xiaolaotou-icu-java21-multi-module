package com.example.config;


import com.example.utils.AuthInterceptor;
import com.example.utils.RequestIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private RequestIdInterceptor requestIdInterceptor;

    @Autowired
    private FileStorageProperties fileStorageProperties;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 保护文章接口
                //.addPathPatterns("/user/info")   // 保护用户信息接口
                .addPathPatterns("/excelbatch/**")   // 保护导入相关接口
                .addPathPatterns("/report/**")
                .excludePathPatterns("/user/login", "/user/register"); // 排除登录注册接口
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(fileStorageProperties.getUploadDir());
        String uploadPath = uploadDir.toAbsolutePath().normalize().toString();

        registry.addResourceHandler(fileStorageProperties.getAccessPath())
                .addResourceLocations("file:" + uploadPath + "/");
    }
}

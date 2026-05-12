package com.example.utils;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ExcelTemplateService {

    @Resource
    private ResourceLoader resourceLoader;

    /**
     * 下载 Excel 模板
     */
    public void downloadTemplate(HttpServletResponse response, String templateName) {
        try {
            // 从 classpath 根目录或 templates 目录读取
            String templatePath = "templates/" + templateName;

            log.info("尝试加载模板文件: {}", templatePath);

            // 使用 ClassPathResource 直接读取
            ClassPathResource resource = new ClassPathResource(templatePath);

            if (!resource.exists()) {
                log.error("模板文件不存在，路径: {}", templatePath);

                // 尝试其他可能的路径
                String alternativePath = "/" + templatePath;
                ClassPathResource altResource = new ClassPathResource(alternativePath);

                if (altResource.exists()) {
                    log.info("使用备用路径加载成功: {}", alternativePath);
                    resource = altResource;
                } else {
                    throw new RuntimeException("模板文件不存在: " + templateName);
                }
            }

            log.info("模板文件加载成功: {}, 大小: {} bytes", templateName, resource.contentLength());

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(templateName, StandardCharsets.UTF_8));
            response.setContentLengthLong(resource.contentLength());

            // 写入文件流
            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            log.info("模板文件下载成功: {}", templateName);

        } catch (IOException e) {
            log.error("下载模板失败: {}", templateName, e);
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }
}

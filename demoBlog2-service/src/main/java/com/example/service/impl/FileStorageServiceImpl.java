package com.example.service.impl;

import com.example.service.IFileStorageService;
import com.example.config.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements IFileStorageService {

    private final Path fileStorageLocation;

    private final FileStorageProperties fileStorageProperties;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建文件存储目录", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long articleId) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFileName.contains("..")) {
            throw new RuntimeException("文件名包含非法路径序列: " + originalFileName);
        }

        validateFileType(file);

        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString() + "_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "." + fileExtension;

        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path targetDirectory = this.fileStorageLocation.resolve(dateFolder);

        try {
            Files.createDirectories(targetDirectory);
        } catch (IOException e) {
            throw new RuntimeException("无法创建日期目录: " + dateFolder, e);
        }

        Path targetLocation = targetDirectory.resolve(newFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = dateFolder + "/" + newFileName;
            log.info("文件存储成功: {}", relativePath);
            return relativePath;

        } catch (IOException ex) {
            throw new RuntimeException("文件存储失败: " + originalFileName, ex);
        }
    }

    @Override
    public String getFileAccessUrl(String fileName) {
        return "/uploads/" + fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("文件删除成功: {}", fileName);
        } catch (IOException ex) {
            log.error("文件删除失败: {}", fileName, ex);
            throw new RuntimeException("文件删除失败: " + fileName, ex);
        }
    }

    @Override
    public byte[] loadFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return Files.readAllBytes(filePath);
            } else {
                throw new RuntimeException("文件不存在: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件路径错误: " + fileName, e);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败: " + fileName, e);
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("无法识别的文件类型");
        }

        boolean allowed = false;
        for (String allowedType : fileStorageProperties.getAllowedFileTypes()) {
            if (contentType.equals(allowedType)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new RuntimeException("不支持的文件类型: " + contentType);
        }

        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new RuntimeException("文件大小超过限制: " +
                    (fileStorageProperties.getMaxFileSize() / 1024 / 1024) + "MB");
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}

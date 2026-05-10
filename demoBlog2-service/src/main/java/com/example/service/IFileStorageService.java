package com.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {

    String storeFile(MultipartFile file, Long articleId);

    String getFileAccessUrl(String fileName);

    void deleteFile(String fileName);

    byte[] loadFile(String fileName);
}

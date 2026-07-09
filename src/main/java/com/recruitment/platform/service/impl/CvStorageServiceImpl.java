package com.recruitment.platform.service.impl;

import com.recruitment.platform.service.CvStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class CvStorageServiceImpl implements CvStorageService {

    @Value("${app.cv.storage.path:uploads/cvs}")
    private String storagePath;

    @Override
    public String store(MultipartFile file, UUID applicantId) {
        try {
            Path storageDir = Paths.get(storagePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";

            String storedFileName = applicantId + "_" + Instant.now().toEpochMilli() + extension;
            Path targetPath = storageDir.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("CV stored successfully at: {}", targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            log.error("Failed to store CV file: {}", e.getMessage());
            throw new RuntimeException("Failed to store CV file", e);
        }
    }

    @Override
    public Resource load(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found or not readable: " + filePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed file path: " + filePath, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("CV file deleted successfully: {}", filePath);
            } else {
                log.warn("CV file not found for deletion: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete CV file: {}", e.getMessage());
        }
    }
}

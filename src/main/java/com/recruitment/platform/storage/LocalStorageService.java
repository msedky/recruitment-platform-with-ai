package com.recruitment.platform.storage;

import jakarta.annotation.PostConstruct;
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
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${app.cv.storage.path}") String storagePath) {
        this.rootLocation = Paths.get(storagePath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Local storage initialised at: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new StorageException("Could not initialise local storage directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, UUID applicantId) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot store empty file");
        }

        String originalFilename = sanitiseFilename(file.getOriginalFilename());
        String storedFilename = applicantId + "_" + originalFilename;
        Path targetPath = rootLocation.resolve(storedFilename).normalize().toAbsolutePath();

        if (!targetPath.startsWith(rootLocation.toAbsolutePath())) {
            throw new StorageException("Cannot store file outside root location: " + originalFilename);
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Stored file locally: {}", targetPath);
            return targetPath.toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + originalFilename, e);
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
            throw new StorageException("File not found or not readable: " + filePath);
        } catch (MalformedURLException e) {
            throw new StorageException("Malformed file path: " + filePath, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.debug("Deleted local file: {}", filePath);
            } else {
                log.warn("Attempted to delete non-existent local file: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete local file: {}", filePath, e);
        }
    }

    private String sanitiseFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
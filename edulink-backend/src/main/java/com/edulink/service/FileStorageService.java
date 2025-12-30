package com.edulink.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Copy file to upload directory
        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = loadFile(filename);
        Files.deleteIfExists(filePath);
    }
}


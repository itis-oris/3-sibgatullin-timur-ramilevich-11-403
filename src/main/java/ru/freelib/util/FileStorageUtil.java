package ru.freelib.util;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.freelib.config.MinioConfig;
import ru.freelib.exception.BusinessException;
import ru.freelib.exception.ExternalServiceException;
import ru.freelib.exception.NotFoundException;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageUtil {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Файл книги обязателен");
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : ".bin";
        String objectName = UUID.randomUUID() + ext;

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("File uploaded to MinIO: {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO", e);
            throw new ExternalServiceException("Ошибка загрузки файла в хранилище", e);
        }
    }

    public void delete(String objectName) {
        if (objectName == null || objectName.isBlank()) return;
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            log.info("File deleted from MinIO: {}", objectName);
        } catch (Exception e) {
            log.warn("Failed to delete file from MinIO: {}", objectName, e);
        }
    }

    public InputStream getInputStream(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get file from MinIO: {}", objectName, e);
            throw new NotFoundException("Файл", e);
        }
    }
}
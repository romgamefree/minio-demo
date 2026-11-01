package com.anhtu.minio.service;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.presign-expire}")
    private int presignExpire;

    public String putObjects(MultipartFile file) throws MinioException, IOException {
        createBucketIfNotExists();

        String objectName = file.getOriginalFilename();

        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .contentType(file.getContentType())
                        .stream(file.getInputStream(), file.getSize(), -1L)
                .build());

        return objectName;
    }

    public void removeObject(String objectName) throws MinioException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).build());
    }

    public String presignedUrl(String objectName) throws MinioException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .method(Http.Method.GET)
                        .expiry(presignExpire, TimeUnit.SECONDS)
                .build());
    }
    private void createBucketIfNotExists() throws MinioException {
        if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }
}
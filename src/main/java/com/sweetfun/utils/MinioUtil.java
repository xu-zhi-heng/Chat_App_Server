package com.sweetfun.utils;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    private void init() {
        createBucketIfNotExists(); // 这里执行时 @Value 已经注入完成
    }

    public String upload(String fileName, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
            // 返回访问 URL（注意：需要 bucket 设置为 public，或者用 presignedUrl）
//            return endpoint + "/" + bucketName + "/" + fileName;
            return fileName;
        } catch (Exception exception) {
            throw new RuntimeException("上传文件到Minio失败: " + exception.getMessage(), exception);
        }
    }


    // 获取临时访问地址
    public String getPresignedUrl(String fileName, int expireSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expireSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("获取预签名URL失败: " + e.getMessage(), e);
        }
    }

    // 自动创建minio bucket
    private void createBucketIfNotExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                System.out.println("✅ 已创建 bucket: " + bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("检查或创建 bucket 失败: " + e.getMessage(), e);
        }
    }
}

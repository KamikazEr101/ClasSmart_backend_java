package com.xinghuo.pro_classify.service.impl;

import com.xinghuo.pro_classify.properties.MinioProperties;
import com.xinghuo.pro_classify.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private final MinioClient client;
    private final MinioProperties properties;

    public FileServiceImpl(MinioClient client, MinioProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    /**
     * @param file 图片文件
     * @return 图片在minio中存储的url
     */
    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean bucketedExists = client.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.getBucketName())
                .build());
        if (!bucketedExists) {
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucketName())
                    .build());
            client.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(properties.getBucketName())
                            .config(createBucketPolicyConfig(properties.getBucketName()))
                            .build());
        }
        InputStream inputStream = file.getInputStream();
        String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        client.putObject(PutObjectArgs.builder()
                .contentType(file.getContentType())
                .bucket(properties.getBucketName())
                .stream(inputStream, file.getSize(), -1)
                .object(fileName)
                .build());
        return String.join("/", properties.getEndPoint(), properties.getBucketName(), fileName);
    }

    /**
     * minio存储策略
     *
     * @param bucketName 桶名
     * @return minio存储策略的json模型
     */
    private String createBucketPolicyConfig(String bucketName) {
        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }

    /**
     * 根据URL删除MinIO中的文件
     *
     * @param fileUrl 文件的完整URL
     */
    @Override
    public void deleteByUrl(String fileUrl) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 从URL中提取对象名称
        // URL格式: endpoint/bucketName/objectName
        String objectName = extractObjectNameFromUrl(fileUrl);
        log.info("正在从Minio中删除文件 {}", objectName);

        if (objectName != null) {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(objectName)
                    .build());
            log.info("成功从Minio中删除文件 {}", objectName);
        } else {
            log.error("从Minio中删除文件失败, 原因: 响应文件不存在 {}", fileUrl);
        }
    }

    /**
     * 从URL中提取对象名称
     *
     * @param fileUrl 文件URL
     * @return 对象名称
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        String endpointWithBucket = String.join("/", properties.getEndPoint(), properties.getBucketName());
        if (fileUrl.startsWith(endpointWithBucket)) {
            return fileUrl.substring(endpointWithBucket.length() + 1);
        }

        return null;
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @return 文件输入流
     */
    @Override
    public InputStream download(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("从Minio中下载文件 {}", objectName);
        return client.getObject(GetObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(objectName)
                .build());
    }

    /**
     * 从URL获取对象名并下载文件
     *
     * @param fileUrl 文件的完整URL
     * @return 文件输入流
     */
    @Override
    public InputStream downloadByUrl(String fileUrl) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String objectName = extractObjectNameFromUrl(fileUrl);
        if (objectName != null) {
            return download(objectName);
        } else {
            log.error("从Minio中下载文件失败, 原因: 无效的URL: {}", fileUrl);
            throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
        }
    }
}

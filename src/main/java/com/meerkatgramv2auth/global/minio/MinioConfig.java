package com.meerkatgramv2auth.global.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "minio")
public record MinioConfig(
        String minioEndpoint, // minio에 접속할 주소
        String minioBucket,
        String minioAccessKey, //계정 ID
        String minioSecretKey, // 비밀번호
        String minioProfilePath, // 프로필 경로
        List<String> allowImageExtensions // 허용할 확장자들
) {
}

package com.meerkatgramv2auth.global.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 스프링 설정 클래스임을 나타냄.
// 스프링이 객체를 생성해 빈으로 관리하며, 기본적으로 싱글톤으로 관리됨.
public class MyMinioClient {

    @Bean
    public MinioClient minioClient(MinioConfig minioConfig) {
        return MinioClient.builder()
                .endpoint(minioConfig.minioEndpoint())
                .credentials(minioConfig.minioAccessKey(), minioConfig.minioSecretKey())
                .build();
    }


}

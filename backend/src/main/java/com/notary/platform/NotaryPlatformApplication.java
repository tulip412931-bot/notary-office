package com.notary.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.notary.platform.mapper")
public class NotaryPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotaryPlatformApplication.class, args);
    }
}

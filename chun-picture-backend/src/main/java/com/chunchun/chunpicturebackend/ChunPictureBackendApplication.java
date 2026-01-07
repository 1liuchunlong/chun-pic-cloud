package com.chunchun.chunpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.chunchun.chunpicturebackend.mapper")
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
public class ChunPictureBackendApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChunPictureBackendApplication.class, args);
    }

}

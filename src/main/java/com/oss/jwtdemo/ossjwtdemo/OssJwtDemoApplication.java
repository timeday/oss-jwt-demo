package com.oss.jwtdemo.ossjwtdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.oss.jwtdemo.ossjwtdemo.*")
public class OssJwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssJwtDemoApplication.class, args);
    }

}

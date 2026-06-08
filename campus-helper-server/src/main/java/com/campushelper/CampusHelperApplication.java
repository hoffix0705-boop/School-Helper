package com.campushelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campushelper.mapper")
public class CampusHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusHelperApplication.class, args);
    }
}
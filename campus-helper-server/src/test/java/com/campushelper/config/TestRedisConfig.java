package com.campushelper.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestRedisConfig {

    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        // 测试环境使用内存 DAO，不依赖 Redis
        return new SaTokenDaoDefaultImpl();
    }
}
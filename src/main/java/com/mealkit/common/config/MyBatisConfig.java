package com.mealkit.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MyBatis 매퍼 스캔 — RDBMS 프로필에서만 활성.
 * {@code inmemory} 일 때는 DataSource/MyBatis 자동설정이 꺼지므로 여기도 제외한다.
 */
@Configuration
@Profile("!inmemory")
@MapperScan("com.mealkit.**.mapper")
public class MyBatisConfig {
}

package com.mealkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 진입점.
 * <p>
 * 회원 저장소 프로필:
 * <ul>
 *   <li>{@code local} (기본) — H2 + MyBatis</li>
 *   <li>{@code mariadb} — MariaDB + MyBatis</li>
 *   <li>{@code inmemory} — DB 없이 ConcurrentHashMap</li>
 * </ul>
 * MyBatis {@code @MapperScan} 은 {@link com.mealkit.common.config.MyBatisConfig} ({@code !inmemory}).
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class MealkitApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealkitApplication.class, args);
    }
}

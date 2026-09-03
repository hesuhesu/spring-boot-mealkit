package com.mealkit.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "dev", "default"})
@OpenAPIDefinition(
        info = @Info(
                title = "Mealkit API",
                version = "0.0.1",
                description = "spring-boot-mealkit API"
        ),
        security = @SecurityRequirement(name = "Authorization")
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth API")
                .packagesToScan("com.mealkit.auth")
                .build();
    }

    @Bean
    public GroupedOpenApi healthApi() {
        return GroupedOpenApi.builder()
                .group("Health API")
                .packagesToScan("com.mealkit.health")
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("Member API")
                .packagesToScan("com.mealkit.member")
                .build();
    }
}

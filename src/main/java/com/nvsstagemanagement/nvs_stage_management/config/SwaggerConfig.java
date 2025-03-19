package com.nvsstagemanagement.nvs_stage_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Quản lý Sân Khấu")
                        .version("1.0")
                        .description("Tài liệu API cho hệ thống quản lý sân khấu"))

                .addSecurityItem(new SecurityRequirement().addList("BearerToken"))

                // Cấu hình Bearer Token cho Swagger UI
                .components(new Components()
                        .addSecuritySchemes("BearerToken", new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập JWT Token của bạn")));
    }
}
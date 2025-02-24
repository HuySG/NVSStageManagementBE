package com.nvsstagemanagement.nvs_stage_management.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                            .description("Tài liệu API cho hệ thống quản lý sân khấu"));
        }
    }



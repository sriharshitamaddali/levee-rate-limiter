package com.levee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SpringConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OpenAPI leveeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Levee API")
                        .description("Rate limiter for external APIs and LLM token management")
                        .version("v1.0"));
    }
}

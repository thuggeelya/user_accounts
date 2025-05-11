package ru.thuggeelya.useraccounts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String TITLE = "User Accounts API";
    private static final String DESCRIPTION = "User Accounts API Documentation";
    private static final String VERSION = "1";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title(TITLE).description(DESCRIPTION).version(VERSION));
    }
}

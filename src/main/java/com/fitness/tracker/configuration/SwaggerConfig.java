package com.fitness.tracker.configuration;

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
                        .title("Fitness Tracker API")
                        .version("1.0.0")
                        .description("API documentation for the Fitness Tracker Application"))
                .components(new Components()
                        .addSecuritySchemes("X-USER-ID",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-USER-ID")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("X-USER-ID"));
    }
}

package com.ds.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hrModuleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinSecure HR Module API")
                        .description("HR Module — manages employee allocation, escalations, appraisals and business reports")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("HR Module Team")
                                .email("bhavvvvvs@gmail.com")))

                // tell Swagger every endpoint needs Bearer JWT
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .name("Bearer Auth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

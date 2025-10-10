package org.example.service.Implementation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI taskFodgeAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFodge API - Smart Task Management System")
                        .version("1.0")
                        .description("TaskFodge is a scalable, secure, and event-driven task management API built with Spring Boot, PostgreSQL, Redis, Kafka, and Docker. It includes JWT-based authentication, role-based access, caching, rate limiting, and observability.")
                        .contact(new Contact()
                                .name("Anweshan Dhara")
                                .url("https://github.com/anweshandha")
                                .email("anweshandhara@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

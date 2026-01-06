package com.fleet.auth_service.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
  @Bean
  public OpenAPI defineOpenApi() {
    Server server = new Server();
    server.setUrl("http://localhost:8080");
    server.setDescription("Development");

    Contact contact = new Contact();
    contact.setName("Thiago Gritti");
    contact.setEmail("tgritti.dev@gmail.com");

    Info information = new Info()
            .title("Fleet Auth Service API")
            .version("1.0.0")
            .description("Sistema de autenticação e autorização para o ecossistema Fleet.")
            .contact(contact);

    SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
            .info(information)
            .servers(List.of(server))
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
  }
}

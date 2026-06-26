package com.read.api.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "URL Shortener Read API",
                version = "1.0.0",
                description = """
                        API responsável pela leitura de URLs,
                        regras de acesso, usuários, tags e métricas.
                        """
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(
                        new io.swagger.v3.oas.models.info.Info()
                                .title("URL Shortener Read API")
                                .version("1.0.0")
                                .description("""
                                        Read Model da plataforma URL Shortener.

                                        Recursos:
                                        - URLs
                                        - Usuários
                                        - Regras de acesso
                                        - Regras de redirecionamento
                                        - Tags
                                        - Métricas
                                        - CDC via Kafka
                                        """)
                                .contact(
                                        new Contact()
                                                .name("Anderson")
                                                .email("anderson.c.rms2005@gmail.com")
                                                .url("https://github.com/andersoncrs5")
                                )
                                .license(
                                        new License()
                                                .name("MIT")
                                                .url("https://opensource.org/licenses/MIT")
                                )
                )

                .addServersItem(
                        new Server()
                                .url("http://localhost:9999")
                                .description("Local")
                )

                .addServersItem(
                        new Server()
                                .url("https://urlshortner.com")
                                .description("Production")
                )

                .tags(
                        List.of(
                                new Tag().name("Users"),
                                new Tag().name("URLs"),
                                new Tag().name("URL Access Rules"),
                                new Tag().name("URL Redirect Rules"),
                                new Tag().name("Tags")
                        )
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT Authorization Token")
                                )
                )

                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub Repository")
                                .url("https://github.com/Andersoncrs5/url-shortener-spring-java")
                );
    }
}
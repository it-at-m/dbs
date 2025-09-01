package de.muenchen.oss.dbs.ticketing.eventing.service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAPIDocumentationConfiguration {

    public static final String BASIC_SCHEME_NAME = "basicScheme";
    private static final String BASIC_SCHEME = "basic";

    private final OpenAPIProperties openAPIProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(openAPIProperties.getName())
                        .description(openAPIProperties.getDescription())
                        .version(openAPIProperties.getVersion()))
                .components(new Components()
                        .addSecuritySchemes(BASIC_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(BASIC_SCHEME)))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_SCHEME));
    }
}

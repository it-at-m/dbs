/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2023
 */
package de.muenchen.dbs.ticketing.dprs.configuration;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfiguration {

    private static final String LOGOUT_URL = "/logout";

    private static final String LOGOUT_SUCCESS_URL = "/loggedout.html";

    /**
     * Same lifetime as SSO Session (e.g. 10 hours).
     */
    @Value("${spring.session.timeout:36000}")
    private long springSessionTimeoutSeconds;

    @Value("${prs.xsrf.disable:false}")
    private boolean disableCsrf;

    /**
     * This method creates the {@link ServerLogoutSuccessHandler} for handling a successful logout.
     * The usage is necessary in {@link SecurityWebFilterChain}.
     *
     * @param uri to forward after an successful logout.
     * @return The handler for forwarding after an succesful logout.
     */
    public static ServerLogoutSuccessHandler createLogoutSuccessHandler(final String uri) {
        final RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(uri));
        return successHandler;
    }

    //---
    //Diese Version ist ganz ohne Security. Das Access-Token wird dann nur im LhmExtIdFilter geprüft.
    //---
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        final CookieServerCsrfTokenRepository tokenRepository = new CookieServerCsrfTokenRepository();
        // requestHandler needed for handling the raw CSRF tokens
        final ServerCsrfTokenRequestAttributeHandler requestHandler = new ServerCsrfTokenRequestAttributeHandler();
        tokenRepository.setCookieCustomizer(responseCookieBuilder -> {
            responseCookieBuilder.domain(".muenchen.de");
            responseCookieBuilder.httpOnly(false);
        });
        // @formatter:off
        return http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll())
                .csrf(csrfSpec -> {
                    if (disableCsrf) {
                        csrfSpec.disable();
                    } else {
                        csrfSpec.csrfTokenRepository(tokenRepository);
                        csrfSpec.csrfTokenRequestHandler(requestHandler);
                    }
                })
                .cors(corsSpec -> corsSpec.configurationSource(exchange -> {
                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.addAllowedHeader("*");

                    configuration.setAllowCredentials(true);

                    configuration.setAllowedMethods(List.of("*"));

                    configuration.addAllowedOriginPattern("https://*.muenchen.de");
                    configuration.addAllowedOriginPattern("https://*.swm.de");
                    configuration.addAllowedOriginPattern("http://localhost:*");

                    return configuration;
                }))
                .build();

        // @formatter:on
    }

}

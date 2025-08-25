package de.muenchen.oss.dbs.ticketing.eventing.service.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * The central class for configuration of all security aspects.
 * Automatically used when not running with profile `no-security`.
 * Configures all endpoints to require authentication via basic auth
 * (except the Spring Boot Actuator endpoints).
 */
@RequiredArgsConstructor
@Configuration
@Profile("!no-security")
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Import(RestTemplateAutoConfiguration.class)
@Slf4j
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests.requestMatchers(
                        // allow access to /actuator/info
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/info"),
                        // allow access to /actuator/health for OpenShift Health Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health"),
                        // allow access to /actuator/health/liveness for OpenShift Liveness Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health/liveness"),
                        // allow access to /actuator/health/readiness for OpenShift Readiness Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health/readiness"),
                        // allow access to SBOM overview
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/sbom"),
                        // allow access to opean-api endpoints
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/v3/api-docs"),
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/v3/api-docs.yaml"),
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/v3/api-docs/**"),
                        // allow access to swagger-ui
                        PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/sbom"),
                        // allow access to SBOM application data
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/sbom/application"),
                        // allow access to /actuator/metrics for Prometheus monitoring in OpenShift
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/metrics"))
                        .permitAll())
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest()
                        .authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}

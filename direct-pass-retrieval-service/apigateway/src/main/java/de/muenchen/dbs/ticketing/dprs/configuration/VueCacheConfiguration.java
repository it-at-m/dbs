/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2023
 */
package de.muenchen.dbs.ticketing.dprs.configuration;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * VueCacheConfigurer setzt Cache Header für die von Vue gebauten Resourcen. Sie enthalten im Namen
 * ein Hash, deswegen sollte das Caching hier keine Probleme machen.
 */
@Configuration
public class VueCacheConfiguration implements WebFluxConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Don't cache loader.js
        registry.addResourceHandler("/loader.js")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache());
        // But cache all src-Files with Version-Hashes, so the webcomponents render quickly
        registry.addResourceHandler("/src/**")
                .addResourceLocations("classpath:/static/src/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
    }
}

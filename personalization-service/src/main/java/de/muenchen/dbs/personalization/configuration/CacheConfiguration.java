package de.muenchen.dbs.personalization.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String SERVICE_NAVIGATOR_SERVICES_CACHE_NAME = "snservices";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(SERVICE_NAVIGATOR_SERVICES_CACHE_NAME);
    }

}

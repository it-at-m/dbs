package de.muenchen.dbs.personalization;

import de.muenchen.dbs.personalization.configuration.CacheConfiguration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheScheduler {

    private final CacheManager cacheManager;

    public CacheScheduler(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void evictSnServicesCache() {
        final Cache cache = cacheManager.getCache(CacheConfiguration.SERVICE_NAVIGATOR_SERVICES_CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
        log.info("Evicted {}-Cache.", CacheConfiguration.SERVICE_NAVIGATOR_SERVICES_CACHE_NAME);
    }
}

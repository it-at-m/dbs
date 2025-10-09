package de.muenchen.dbs.personalization;

import de.muenchen.dbs.personalization.configuration.CacheConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheScheduler {

    @Autowired
    CacheManager cacheManager;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void evictSnServicesCache() {
        cacheManager.getCache(CacheConfiguration.SERVICE_NAVIGATOR_SERVICES_CACHE_NAME).clear();
        log.info("Evicted {}-Cache.", CacheConfiguration.SERVICE_NAVIGATOR_SERVICES_CACHE_NAME);
    }
}

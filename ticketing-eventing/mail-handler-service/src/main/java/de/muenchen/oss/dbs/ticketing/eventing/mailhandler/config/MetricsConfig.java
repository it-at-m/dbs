package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.config;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Need to disable the kafka offset metric for now due to a bug
 * https://github.com/spring-cloud/spring-cloud-stream/issues/3208
 */
@Configuration
public class MetricsConfig {

    @Bean
    public MeterFilter disableKafkaOffsetMetrics() {
        return MeterFilter.denyNameStartsWith("spring.cloud.stream.binder.kafka.offset");
    }

}

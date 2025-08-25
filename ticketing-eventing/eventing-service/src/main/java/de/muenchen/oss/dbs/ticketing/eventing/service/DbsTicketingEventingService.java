package de.muenchen.oss.dbs.ticketing.eventing.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application class for starting the microservice.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class DbsTicketingEventingService {
    public static void main(final String[] args) {
        SpringApplication.run(DbsTicketingEventingService.class, args);
    }
}

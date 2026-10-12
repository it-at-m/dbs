package de.muenchen.oss.dbs.ticketing.eventing.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application class for starting the microservice.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class DbsTicketingEventingDmsService {
    public static void main(final String[] args) {
        SpringApplication.run(DbsTicketingEventingDmsService.class, args);
    }
}

package de.muenchen.dbs.personalization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application class for starting the microservice.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@SuppressWarnings("PMD.UseUtilityClass")
public class PersonalizationServiceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PersonalizationServiceApplication.class, args);
    }
}

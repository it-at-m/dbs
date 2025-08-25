package de.muenchen.oss.dbs.ticketing.eventing.service.configuration;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("dbs.eventing")
@Getter
@Setter
public class DbsEventingProperties {
    @NotNull private Map<String, String> triggerMapping;
}

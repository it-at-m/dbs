package de.muenchen.oss.dbs.ticketing.eventing.dms.config;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("dbs.eventing.dms")
@Validated
@Getter
@Setter
public class DmsProperties {
    @NotNull
    private List<String> relevantActions;

    @NotNull
    private List<String> articleActions;
}

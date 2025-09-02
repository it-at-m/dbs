package de.muenchen.dbs.personalization.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "p13n")
@Validated
@Profile("!no-security")
@Data
public class P13nConfiguration {
    /**
     * ServiceNavigator API URL.
     */
    @NotBlank
    private String serviceNavigatorUrl = "https://stadt.muenchen.de/service/rs/befi/navigator";

    /**
     * (optional) Proxy Host used to call ServiceNavigator.
     */
    private String proxyHost = null;

    /**
     * (optional) Proxy Port used to call ServiceNavigator.
     */
    private int proxyPort = 80;

}

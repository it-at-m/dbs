package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("dbs.eventing.mail")
@Validated
@Getter
@Setter
public class MailHandlerProperties {
    @NotBlank
    private String recipient;
    @NotNull
    private List<String> relevantTicketTypes;
}

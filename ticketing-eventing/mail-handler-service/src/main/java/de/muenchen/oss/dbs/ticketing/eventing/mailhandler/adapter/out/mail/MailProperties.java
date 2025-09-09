package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("dbs.eventing.mail.smtp")
@Validated
@Getter
@Setter
public class MailProperties {
    @NotBlank
    private String fromAddress;
}

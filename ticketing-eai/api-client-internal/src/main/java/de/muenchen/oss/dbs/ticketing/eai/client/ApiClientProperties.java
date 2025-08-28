package de.muenchen.oss.dbs.ticketing.eai.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("dbs.eai.client")
@Validated
public class ApiClientProperties {
    /**
     * Base URL of the ticket-eai-service to make API calls against.
     */
    @NotBlank
    private String eaiBaseUrl;
    /**
     * Name of the client registration configured via Spring oAuth client to use for authenticating API requests.
     */
    @NotBlank
    private String oauthClientRegistration = "zammad";

    public String getEaiBaseUrl() {
        return eaiBaseUrl;
    }

    public void setEaiBaseUrl(String eaiBaseUrl) {
        this.eaiBaseUrl = eaiBaseUrl;
    }

    public String getOauthClientRegistration() {
        return oauthClientRegistration;
    }

    public void setOauthClientRegistration(String oauthClientRegistration) {
        this.oauthClientRegistration = oauthClientRegistration;
    }
}

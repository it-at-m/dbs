package de.muenchen.dbs.ticketing.dprs.configuration;

import de.muenchen.dbs.ticketing.dprs.clients.ApiClient;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.TicketsApi;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.UsersApi;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiClientConfiguration {

    @Value("${prs.eai.urlBase}")
    private String zammadEaiUrlBase;

    @Bean
    public TicketsApi ticketsApi(final ApiClient apiClient) {
        return new TicketsApi(apiClient);
    }

    @Bean
    public UsersApi usersApi(final ApiClient apiClient) {
        return new UsersApi(apiClient);
    }

    @Bean
    public ApiClient apiClient(final WebClient restTemplate) {
        val apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(zammadEaiUrlBase);
        return apiClient;
    }

    @Bean
    WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrations,
                new WebSessionServerOAuth2AuthorizedClientRepository());
        oauth.setDefaultClientRegistrationId("sso");
        return WebClient.builder()
                .filter(oauth)
                .build();
    }

}

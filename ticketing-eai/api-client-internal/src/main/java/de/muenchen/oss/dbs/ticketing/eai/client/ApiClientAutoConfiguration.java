package de.muenchen.oss.dbs.ticketing.eai.client;

import de.muenchen.oss.dbs.ticketing.eai.client.api.ArticlesApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.AttachmentsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.OrganizationsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.UsersApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties({ApiClientProperties.class})
public class ApiClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ApiClient apiClient(final ApiClientProperties apiClientProperties,
                               final ClientRegistrationRepository clientRegistrationRepository,
                               final OAuth2AuthorizedClientService authorizedClientService) {
        final ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService));
        oauth.setDefaultClientRegistrationId(apiClientProperties.getOauthClientRegistration());
        final WebClient webClient = ApiClient.buildWebClientBuilder()
                .apply(oauth.oauth2Configuration())
                .build();
        final ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(apiClientProperties.getEaiBaseUrl());
        return apiClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public TicketsApi ticketsApi(final ApiClient apiClient) {
        return new TicketsApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public ArticlesApi articlesApi(final ApiClient apiClient) {
        return new ArticlesApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public AttachmentsApi attachmentsApi(final ApiClient apiClient) {
        return new AttachmentsApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public OrganizationsApi organizationsApi(final ApiClient apiClient) {
        return new OrganizationsApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public UsersApi usersApi(final ApiClient apiClient) {
        return new UsersApi(apiClient);
    }
}

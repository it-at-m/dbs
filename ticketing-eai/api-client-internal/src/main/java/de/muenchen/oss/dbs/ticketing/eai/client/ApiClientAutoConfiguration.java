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
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties({ApiClientProperties.class})
public class ApiClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ApiClient apiClient(final ApiClientProperties apiClientProperties,
                               final ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        final ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauthFilter.setDefaultClientRegistrationId(apiClientProperties.getOauthClientRegistration());

        final WebClient webClient = ApiClient.buildWebClientBuilder()
                .filter(oauthFilter)
                .build();

        final ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(apiClientProperties.getEaiBaseUrl());
        return apiClient;
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrationRepository,
                                                                         ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build();
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
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

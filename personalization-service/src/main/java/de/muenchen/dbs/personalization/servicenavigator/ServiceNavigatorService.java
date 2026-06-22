package de.muenchen.dbs.personalization.servicenavigator;

import de.muenchen.dbs.personalization.configuration.CacheConfiguration;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class ServiceNavigatorService {

    private static final String SERVICENAVIGATOR_QUERY_PARAMETER_ID = "?id=";
    public static final String SERVICENAVIGATOR_FALLBACK_LANGUAGE = "de";

    private final RestTemplate restTemplate;
    private final P13nConfiguration p13nConfiguration;

    public ServiceNavigatorService(final P13nConfiguration p13nConfiguration) {
        this.p13nConfiguration = p13nConfiguration;
        if (StringUtils.isBlank(p13nConfiguration.getProxyHost())) {
            this.restTemplate = new RestTemplate();
        } else {
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(p13nConfiguration.getProxyHost(), p13nConfiguration.getProxyPort()));
            final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);
            this.restTemplate = new RestTemplate(requestFactory);
        }
    }

    @Cacheable(CacheConfiguration.SERVICE_NAVIGATOR_SERVICES_CACHE_NAME)
    public Optional<ServiceNavigatorResponse> getServiceNavigatorService(final String serviceId, final String lang) {
        final String url = p13nConfiguration.getServiceNavigatorUrl()
                + SERVICENAVIGATOR_QUERY_PARAMETER_ID + URLEncoder.encode(serviceId, StandardCharsets.UTF_8)
                + "&lang=" + URLEncoder.encode(lang, StandardCharsets.UTF_8);

        try {
            log.debug("#getServiceNavigatorService: Loading {}", url);
            HttpHeaders headers = new HttpHeaders();
            if (p13nConfiguration.getServiceNavigatorBasicAuth() != null && !p13nConfiguration.getServiceNavigatorBasicAuth().isBlank()) {
                headers.add("Authorization", "Basic " + p13nConfiguration.getServiceNavigatorBasicAuth());
            }
            HttpEntity<String> request = new HttpEntity<String>(headers);
            final ResponseEntity<ServiceNavigatorResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ServiceNavigatorResponse.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return Optional.ofNullable(response.getBody());
            } else {
                log.warn("Searching Service with Service-ID {} and lang {} returned a !2xx HTTP Response of {}. Returning empty.", serviceId, lang,
                        response.getStatusCode());
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            // If translation is not available (400) and requested lang is not 'de', retry once with 'de'
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST && !SERVICENAVIGATOR_FALLBACK_LANGUAGE.equals(lang)) {
                return getServiceNavigatorService(serviceId, SERVICENAVIGATOR_FALLBACK_LANGUAGE);
            }
            log.error("HTTP Client-Error {} when trying to fetch Service with Service-ID {} and lang {}: {}. Returning empty response.", e.getStatusCode(),
                    serviceId,
                    lang, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Network Error when trying to fetch Service with Service-ID {} and lang {}. Throwing Service Unavailable.", serviceId, lang, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "ServiceNavigator was unreachable", e);
        }
    }

}

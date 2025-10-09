package de.muenchen.dbs.personalization.servicenavigator;

import de.muenchen.dbs.personalization.checklist.domain.ChecklistItemServiceNavigatorDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.OnlineServiceDTO;
import de.muenchen.dbs.personalization.configuration.CacheConfiguration;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;

@Service
@Slf4j
public class ServiceNavigatorService {

    private static final String SERVICENAVIGATOR_QUERY_PARAMETER_ID = "?id=";

    private final RestTemplate restTemplate;
    private final P13nConfiguration p13nConfiguration;

    public ServiceNavigatorService(final P13nConfiguration p13nConfiguration, final ChecklistMapper checklistMapper) {
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
    public Optional<ServiceNavigatorResponse> getServiceNavigatorService(String serviceId) {
        final String url = p13nConfiguration.getServiceNavigatorUrl() + SERVICENAVIGATOR_QUERY_PARAMETER_ID + serviceId;

        try {
            log.debug("#getServiceNavigatorService: Loading {}", url);
            final ResponseEntity<ServiceNavigatorResponse> response = restTemplate.getForEntity(url, ServiceNavigatorResponse.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return Optional.of(response.getBody());
            } else {
                log.warn("Searching Service with Service-ID {} returned a !2xx HTTP Response of {}. Returning empty.", serviceId, response.getStatusCode());
                return Optional.empty();

            }
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Error {} when trying to fetch Service with Service-ID {}: {}", e.getStatusCode(), serviceId,  e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Network Error when trying to fetch Service with Service-ID {}", serviceId, e);
            return Optional.empty();
        }
    }

    public ChecklistItemServiceNavigatorDTO toDto(final ServiceNavigatorResponse snResponse) {
        //todo make nice with mapstruct
        final ChecklistItemServiceNavigatorDTO mappedDto = new ChecklistItemServiceNavigatorDTO();
        mappedDto.setServiceID(snResponse.id());
        mappedDto.setTitle(snResponse.serviceName());
        mappedDto.setNote(snResponse.summary());
        mappedDto.setPublicUrl(snResponse.publicUrl());
        mappedDto.setRequired(snResponse.mandatory());
        mappedDto.setIsExternal(snResponse.isExternal());
        mappedDto.setAppointmentService(snResponse.appointmentService());
        mappedDto.setAppointmentServiceUrl(snResponse.appointmentServiceUrl());
        if (snResponse.onlineServices() != null) {
            mappedDto.setOnlineServices(snResponse.onlineServices().stream().map(onlineService -> {
                final OnlineServiceDTO mappedOSDTO = new OnlineServiceDTO();
                mappedOSDTO.setUri(onlineService.uri());
                mappedOSDTO.setLabel(onlineService.label());
                return mappedOSDTO;
            }).toList());
        }
        return mappedDto;
    }
}

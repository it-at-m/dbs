package de.muenchen.dbs.personalization.servicenavigator;

import de.muenchen.dbs.personalization.checklist.domain.*;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ServiceNavigatorService {

    private static final String SERVICENAVIGATOR_QUERY_PARAMETER_ID = "?id=";

    private final RestTemplate restTemplate;
    private final P13nConfiguration p13nConfiguration;
    private final ChecklistMapper checklistMapper;

    public ServiceNavigatorService(final P13nConfiguration p13nConfiguration, final ChecklistMapper checklistMapper) {
        this.p13nConfiguration = p13nConfiguration;
        this.checklistMapper = checklistMapper;
        if (StringUtils.isBlank(p13nConfiguration.getProxyHost())) {
            this.restTemplate = new RestTemplate();
        } else {
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(p13nConfiguration.getProxyHost(), p13nConfiguration.getProxyPort()));
            final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);
            this.restTemplate = new RestTemplate(requestFactory);
        }
    }

    public ChecklistServiceNavigatorReadDTO getChecklistServiceNavigatorReadDTO(final Checklist checklist) {
        final String serviceIDs = checklist.getChecklistItems().stream()
                .map(ChecklistItem::getServiceID)
                .collect(Collectors.joining(","));

        final String url = p13nConfiguration.getServiceNavigatorUrl() + SERVICENAVIGATOR_QUERY_PARAMETER_ID + serviceIDs;
        List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDTOList = new ArrayList<>();

        log.debug("Get all service infos from {}", url);
        try {
            final ResponseEntity<List<ServiceNavigatorResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            checklistItemServiceNavigatorDTOList = toDto(response.getBody());

        } catch (final Exception e) {
            log.error("Error retrieving ServiceNavigator data from {}", url, e);
            for (final ChecklistItem item : checklist.getChecklistItems()) {
                final ChecklistItemServiceNavigatorDTO itemServiceNavigatorDTO = new ChecklistItemServiceNavigatorDTO();
                itemServiceNavigatorDTO.setServiceID(item.getServiceID());
                itemServiceNavigatorDTO.setNote(item.getNote());
                itemServiceNavigatorDTO.setTitle(item.getTitle());
                itemServiceNavigatorDTO.setRequired(item.getRequired());
                itemServiceNavigatorDTO.setChecked(item.getChecked());
                checklistItemServiceNavigatorDTOList.add(itemServiceNavigatorDTO);
            }
        }

        checklistItemServiceNavigatorDTOList.forEach(itemDto -> itemDto.setChecked(
                checklist.getChecklistItems().stream()
                        .filter(checklistItem -> checklistItem.getServiceID().equals(itemDto.getServiceID()))
                        .findFirst()
                        .orElseGet(ChecklistItem::new)
                        .getChecked()));

        final ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO = checklistMapper.toServiceNavigatorReadDTO(checklist);
        checklistServiceNavigatorReadDTO.setChecklistItemServiceNavigatorDtos(checklistItemServiceNavigatorDTOList);
        return checklistServiceNavigatorReadDTO;

    }

    public List<ChecklistItemServiceNavigatorDTO> getChecklistServiceNavigatorReadDTO(final String serviceIds) {
        final String url = p13nConfiguration.getServiceNavigatorUrl() + SERVICENAVIGATOR_QUERY_PARAMETER_ID + serviceIds;
        final ResponseEntity<List<ServiceNavigatorResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return toDto(response.getBody());
    }

    private List<ChecklistItemServiceNavigatorDTO> toDto(final List<ServiceNavigatorResponse> snResponseList) {
        //todo make nice with mapstruct
        return Objects.requireNonNull(snResponseList).stream().map(snResponse -> {
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
        }).toList();
    }
}

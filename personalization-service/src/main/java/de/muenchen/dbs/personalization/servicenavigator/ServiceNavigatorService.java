package de.muenchen.dbs.personalization.servicenavigator;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItem;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItemServiceNavigatorDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistServiceNavigatorReadDTO;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
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
            ResponseEntity<List<ChecklistItemServiceNavigatorDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            checklistItemServiceNavigatorDTOList = response.getBody();
        } catch (final Exception e) {
            log.error("Error retrieving ServiceNavigator data from {}", url);
            for (final ChecklistItem item : checklist.getChecklistItems()) {
                final ChecklistItemServiceNavigatorDTO itemServiceNavigatorDTO = new ChecklistItemServiceNavigatorDTO();
                itemServiceNavigatorDTO.setServiceID(item.getServiceID());
                itemServiceNavigatorDTO.setSummary(item.getNote());
                itemServiceNavigatorDTO.setServiceName(item.getTitle());
                itemServiceNavigatorDTO.setMandatory(item.getRequired());
                checklistItemServiceNavigatorDTOList.add(itemServiceNavigatorDTO);
            }
        }
        final ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO = checklistMapper.toServiceNavigatorReadDTO(checklist);
        checklistServiceNavigatorReadDTO.setChecklistItemServiceNavigatorDtos(checklistItemServiceNavigatorDTOList);
        return checklistServiceNavigatorReadDTO;

    }
}

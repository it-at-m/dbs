package de.muenchen.dbs.personalization.servicenavigator;

import de.muenchen.dbs.personalization.checklist.domain.ChecklistItemServiceNavigatorDTO;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/public/servicenavigator")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Public ServiceNavigator Endpoints", description = "Endpoints not requiring authentication")
public class PublicServiceNavigatorController {

    private static final String SERVICENAVIGATOR_QUERY_PARAMETER_ID = "?id=";
    public static final String SERVICE_IDS_SEPARATOR = ",";

    private final RestTemplate restTemplate;
    private final P13nConfiguration p13nConfiguration;
    private final ServiceNavigatorService snService;

    @Autowired
    public PublicServiceNavigatorController(final P13nConfiguration p13nConfiguration, final ServiceNavigatorService snService) {
        this.p13nConfiguration = p13nConfiguration;
        this.snService = snService;

        if (StringUtils.isBlank(p13nConfiguration.getProxyHost())) {
            this.restTemplate = new RestTemplate();
        } else {
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(p13nConfiguration.getProxyHost(), p13nConfiguration.getProxyPort()));
            final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);
            this.restTemplate = new RestTemplate(requestFactory);
        }
    }

    @GetMapping
    @Operation(summary = "Lookup ServiceNavigator Services by ServiceID. Returns a list of services for the given service IDs.")
    public List<ChecklistItemServiceNavigatorDTO> getServicesByIds(@RequestParam("ids") final String serviceIds) {
        return Arrays.stream(serviceIds.split(SERVICE_IDS_SEPARATOR))
                .map(snService::getServiceNavigatorService)
                .flatMap(Optional::stream)
                .map(snService::toDto)
                .toList();
    }

}

package de.muenchen.dbs.personalization.servicenavigator;

import java.util.List;

public record ServiceNavigatorResponse(
        String serviceName,
        String publicUrl,
        String summary,
        String id,
        Boolean isExternal,
        Boolean appointmentService,
        String appointmentServiceUrl,
        Boolean mandatory,
        List<OnlineService> onlineServices) {
}

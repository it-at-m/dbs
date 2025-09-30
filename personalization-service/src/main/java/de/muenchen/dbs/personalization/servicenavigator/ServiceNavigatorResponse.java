package de.muenchen.dbs.personalization.servicenavigator;

import java.util.List;

public record ServiceNavigatorResponse(
        String serviceName,
        String publicUrl,
        String summary,
        String id,
        boolean isExternal,
        boolean appointmentService,
        String appointmentServiceUrl,
        boolean mandatory,
        List<OnlineService> onlineServices) {
}

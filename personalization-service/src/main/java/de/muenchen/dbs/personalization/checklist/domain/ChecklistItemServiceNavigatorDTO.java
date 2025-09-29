package de.muenchen.dbs.personalization.checklist.domain;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ChecklistItemServiceNavigatorDTO {

    private String serviceID;

    private ZonedDateTime checked;

    private String serviceName;

    private String publicUrl;

    private String summary;

    private Boolean isExternal;

    private Boolean mandatory;

    private String appointmentServiceUrl;

    private Boolean appointmentService;

    private List<OnlineServiceDTO> onlineServices;
}

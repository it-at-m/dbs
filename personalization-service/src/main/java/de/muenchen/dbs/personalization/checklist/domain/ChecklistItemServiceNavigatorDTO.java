package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.persistence.Embedded;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ChecklistItemServiceNavigatorDTO {

    private String serviceID;

    private ZonedDateTime checked;

    private String title;

    private String publicUrl;

    private String note;

    private Boolean isExternal;

    private Boolean required;

    private String appointmentServiceUrl;

    @Embedded
    private List<OnlineServiceDTO> onlineServices;
}

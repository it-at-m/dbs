package de.muenchen.dbs.personalization.checklist.domain;

import lombok.Data;

import java.time.ZonedDateTime;

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
}

package de.muenchen.dbs.personalization.checklist.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ChecklistServiceNavigatorReadDTO {

    private UUID id;

    private String lhmExtId;

    private String title;

    private ZonedDateTime lastUpdate;

    private List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDtos;
}

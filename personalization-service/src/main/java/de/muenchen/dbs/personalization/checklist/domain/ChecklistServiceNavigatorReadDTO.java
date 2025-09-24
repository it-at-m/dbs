package de.muenchen.dbs.personalization.checklist.domain;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ChecklistServiceNavigatorReadDTO {

    private String lhmExtId;

    private String title;

    private ZonedDateTime lastUpdate;

    private List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDtos;
}

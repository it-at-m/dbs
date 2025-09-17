package de.muenchen.dbs.personalization.checklist.domain;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ChecklistServiceNavigatorReadDTO {

    private String lhmExtId;

    private String title;

    private ZonedDateTime lastUpdate;

    private List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDtos;
}

package de.muenchen.dbs.personalization.checklist.domain;

import java.time.ZonedDateTime;

public record ChecklistItemDTO(String serviceID, ZonedDateTime checked, String title, String note, Boolean required) {

}

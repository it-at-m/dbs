package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ChecklistReadDTO(@NotNull UUID id, @NotNull String lhmExtId, @NotNull String title, @NotNull ZonedDateTime lastUpdate,
        @NotNull List<ChecklistItemDTO> checklistItems) {

}

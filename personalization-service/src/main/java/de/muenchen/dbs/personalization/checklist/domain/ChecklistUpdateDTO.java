package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ChecklistUpdateDTO(@NotNull UUID id, @NotNull String email, @NotNull String title, @NotNull List<ChecklistItemDTO> checklistItems) {

}

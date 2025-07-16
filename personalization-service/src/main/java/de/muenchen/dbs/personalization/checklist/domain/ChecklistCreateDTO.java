package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChecklistCreateDTO(@NotNull String title, @NotNull List<ChecklistItemDTO> checklistItems) {

}

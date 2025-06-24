package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChecklistCreateDTO(@NotNull String lhmExtId, @NotNull List<String> checklistItems) {

}

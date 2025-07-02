package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChecklistCreateDTO(@NotNull String lhmExtId, @NotNull String title, @NotNull List<String> checklistItems) {

}

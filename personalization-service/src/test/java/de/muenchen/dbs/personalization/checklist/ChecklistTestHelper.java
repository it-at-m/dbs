package de.muenchen.dbs.personalization.checklist;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItem;
import java.util.List;
import java.util.UUID;

public final class ChecklistTestHelper {

    private ChecklistTestHelper() {
    }

    public static Checklist createTestChecklist(final UUID id, final String email, final List<ChecklistItem> checklistItems) {
        final Checklist checklist = new Checklist();
        if (id != null) {
            checklist.setId(id);
        }
        checklist.setEmail(email);
        checklist.setTitle("title");
        if (checklistItems != null) {
            checklist.setChecklistItems(checklistItems);
        } else {
            final ChecklistItem checklistItem1 = new ChecklistItem();
            final ChecklistItem checklistItem2 = new ChecklistItem();
            checklistItem1.setServiceID("item1");
            checklistItem2.setServiceID("item2");
            checklist.setChecklistItems(List.of(checklistItem1, checklistItem2));
        }
        return checklist;
    }

}

package de.muenchen.dbs.personalization.checklist.domain;

import de.muenchen.dbs.personalization.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Checklist extends BaseEntity {

    private String lhmExtId;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "checklist_id"))
    private List<ChecklistItem> checklistItems;

}

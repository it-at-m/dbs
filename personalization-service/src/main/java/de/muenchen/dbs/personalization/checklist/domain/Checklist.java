package de.muenchen.dbs.personalization.checklist.domain;

import de.muenchen.dbs.personalization.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.time.ZonedDateTime;
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

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "lhm_ext_id", nullable = false, length = 1024)
    @NotNull
    private String lhmExtId;

    @Column(name = "last-update")
    private ZonedDateTime lastUpdate;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "checklist_id"))
    private List<ChecklistItem> checklistItems;

}

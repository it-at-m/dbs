package de.muenchen.dbs.personalization.checklist.domain;

import de.muenchen.dbs.personalization.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
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

    @Column(name = "lhm_ext_id", nullable = false)
    @NotNull
    private String lhmExtId;

    @Column(name = "situation_id", nullable = true)
    private String situationId;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "last_update")
    private ZonedDateTime lastUpdate;

    @ElementCollection
    @CollectionTable(name = "checklist_item", joinColumns = @JoinColumn(name = "checklist_id"))
    @OrderColumn(name = "checklistitem_order")
    private List<ChecklistItem> checklistItems;
}

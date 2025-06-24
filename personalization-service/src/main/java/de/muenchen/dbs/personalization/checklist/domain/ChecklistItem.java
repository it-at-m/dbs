package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {
    private String serviceID;
    private Boolean checked;
    private String title;
    private String note;
    private Boolean required;
}

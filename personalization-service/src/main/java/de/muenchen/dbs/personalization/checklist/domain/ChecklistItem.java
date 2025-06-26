package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.ZonedDateTime;
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

    @Column(name = "service_id", length = 1024)
    private String serviceID;

    @Column(name = "checked")
    private ZonedDateTime checked;

    @Column(name = "title", length = 1024)
    private String title;

    @Column(name = "note", length = 1024)
    private String note;

    @Column(name = "required")
    private Boolean required;
}

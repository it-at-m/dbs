package de.muenchen.dbs.personalization.checklist.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OnlineServiceDTO {

    private String uri;

    private String label;
}

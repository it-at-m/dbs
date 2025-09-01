package de.muenchen.dbs.personalization.servicefinder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
@Entity
public class ServiceFinder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "service_id")
    private String serviceID;

    private ZonedDateTime lastUpdate;

    private String title;

    private String note;
}

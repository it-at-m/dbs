package de.muenchen.oss.dbs.ticketing.eventing.service.core.port.in;

import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface HandleEventInPort {
    void handleEvent(@NotNull @Valid Event event);
}

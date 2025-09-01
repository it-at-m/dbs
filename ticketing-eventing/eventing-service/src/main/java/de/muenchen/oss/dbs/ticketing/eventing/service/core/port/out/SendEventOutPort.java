package de.muenchen.oss.dbs.ticketing.eventing.service.core.port.out;

import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;

public interface SendEventOutPort {
    void sendEvent(Event event);
}

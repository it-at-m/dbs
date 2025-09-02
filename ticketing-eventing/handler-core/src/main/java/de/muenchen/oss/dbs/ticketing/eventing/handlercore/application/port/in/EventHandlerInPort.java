package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in;

import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;

public interface EventHandlerInPort {
    void handleEvent(Event event);
}

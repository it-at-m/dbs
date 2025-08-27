package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;

public interface TicketingOutPort {
    TicketInternal getTicket(String ticketId);
}

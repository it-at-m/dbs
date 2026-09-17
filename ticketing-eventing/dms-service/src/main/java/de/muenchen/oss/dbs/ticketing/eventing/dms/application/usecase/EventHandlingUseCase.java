package de.muenchen.oss.dbs.ticketing.eventing.dms.application.usecase;

import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eventing.dms.config.DmsProperties;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in.EventHandlerInPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlingUseCase implements EventHandlerInPort {

    private final DmsProperties dmsProperties;
    private final TicketingOutPort ticketingOutPort;

    @Override
    public void handleEvent(final Event event) {
        // check if should handle event
        if (!isRelevantEvent(event)) {
            log.debug("Event not relevant");
            return;
        }
        log.info("Handling event");
        try {
            // find event ticket
            final TicketInternal ticket = ticketingOutPort.getTicket(event.ticket());
            //check if the ticket contains all relevant data
            if (!ticketMeetsRequirements(ticket)) {
                log.debug("Ticket not relevant");
                return;
            }

            log.info("Event handled successfully");
        } catch (final Exception e) {
            log.error("Exception during event handling: {}", e.getMessage());
        }
    }

    private boolean isRelevantEvent(final Event event) {
        log.debug("checking event: {}", event);
        return dmsProperties.getRelevantActions().contains(event.action());
    }

    private boolean ticketMeetsRequirements(final TicketInternal ticket) {
        log.debug("Checking ticket requirements: {}", ticket.getEakteCooTarget());
        return isPresent(ticket.getEakteUserlogin())
                && isPresent(ticket.getEakteJoboe())
                && isPresent(ticket.getEakteCooTarget());
    }

    private boolean isPresent(final String str) {
        return str != null && !str.isEmpty();
    }
}

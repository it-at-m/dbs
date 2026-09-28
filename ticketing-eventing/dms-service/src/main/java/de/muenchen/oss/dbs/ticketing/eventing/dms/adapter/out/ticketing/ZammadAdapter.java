package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.ticketing;

import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.ticketing.TicketHistoryOutPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
@RequiredArgsConstructor
@Slf4j
public class ZammadAdapter implements TicketHistoryOutPort {
    private final TicketsApi ticketsApi;

    @Override
    public String getTicketHistory(final String ticketId) {
        try {
            return ticketsApi.getTicketHistory(ticketId).block();
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting ticket history with id %s failed: %s"
                    .formatted(ticketId, e.getResponseBodyAsString()), e);
        }
    }
}

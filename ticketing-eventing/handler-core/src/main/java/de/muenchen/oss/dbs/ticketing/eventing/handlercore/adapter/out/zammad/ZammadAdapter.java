package de.muenchen.oss.dbs.ticketing.eventing.handlercore.adapter.out.zammad;

import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class ZammadAdapter implements TicketingOutPort {
    private final TicketsApi ticketsApi;

    @Override
    public TicketInternal getTicket(final String ticketId) {
        try {
            return ticketsApi.getTicketByIdWithUser(ticketId, null, null, null).block();
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting ticket with id %s failed: %s"
                    .formatted(ticketId, e.getResponseBodyAsString()), e);
        }
    }

    @Override
    public InputStream getAttachmentContent(final String attachmentId) {
        throw new IllegalStateException("Not implemented");
    }
}

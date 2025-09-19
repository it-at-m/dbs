package de.muenchen.oss.dbs.ticketing.eventing.handlercore.adapter.out.zammad;

import de.muenchen.oss.dbs.ticketing.eai.client.api.AttachmentsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTO;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZammadAdapter implements TicketingOutPort {
    private final TicketsApi ticketsApi;
    private final AttachmentsApi attachmentsApi;

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
    public TicketInternal updateTicket(final UpdateTicketDTO updateTicketDTO) {
        assert updateTicketDTO != null;
        //TODO use v2 here --> article has to come out as mandatory-param for that
        try {
            final ResponseEntity<TicketInternal> response = ticketsApi.updateTicket1WithHttpInfo(updateTicketDTO.getId(), updateTicketDTO, null, null).block();
            return response != null ? response.getBody() : null;
        } catch (WebClientResponseException e) {
            log.error(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getAttachmentContent(final String ticketId, final String articleId, final String attachmentId) {
        try {
            final DataBuffer response = attachmentsApi.getAttachmentWithResponseSpec(ticketId, articleId, attachmentId).bodyToMono(DataBuffer.class).block();
            assert response != null;
            return response.asInputStream();
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting attachment with id %s failed: %s"
                    .formatted(attachmentId, e.getResponseBodyAsString()), e);
        }
    }
}

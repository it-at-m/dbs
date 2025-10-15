package de.muenchen.oss.dbs.ticketing.eventing.handlercore.adapter.out.zammad;

import de.muenchen.oss.dbs.ticketing.eai.client.api.AttachmentsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTOV2;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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
    public TicketInternal updateTicket(final UpdateTicketDTOV2 updateTicketDTO, final Collection<AbstractResource> attachments) {
        assert updateTicketDTO != null && updateTicketDTO.getId() != null;
        log.debug("Updating ticket {}: {}, attachments: {}", updateTicketDTO.getId(), updateTicketDTO, attachments);
        try {
            final ResponseEntity<TicketInternal> response = ticketsApi
                    .updateTicketWithHttpInfo(updateTicketDTO.getId(), updateTicketDTO, null, null, attachments).block();
            log.info("Updated ticket {}", updateTicketDTO.getId());
            return response != null ? response.getBody() : null;
        } catch (WebClientResponseException e) {
            log.error(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getAttachmentContent(final String ticketId, final String articleId, final String attachmentId) {
        try {
            final Flux<DataBuffer> response = attachmentsApi.getAttachmentWithResponseSpec(ticketId, articleId, attachmentId)
                    .bodyToFlux(DataBuffer.class);
            final PipedOutputStream outputStream = new PipedOutputStream();
            DataBufferUtils.write(response, outputStream)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnTerminate(() -> {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException("Error while closing OutputStream", e);
                        }
                    }).subscribe();
            return new PipedInputStream(outputStream);
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting attachment with id %s failed: %s"
                    .formatted(attachmentId, e.getResponseBodyAsString()), e);
        } catch (final IOException e) {
            throw new ZammadApiException("Exception while streaming attachment (id: %s) content"
                    .formatted(attachmentId), e);
        }
    }
}

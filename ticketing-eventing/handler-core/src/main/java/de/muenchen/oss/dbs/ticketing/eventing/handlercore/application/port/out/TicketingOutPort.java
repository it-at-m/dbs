package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTO;
import java.io.InputStream;

public interface TicketingOutPort {
    TicketInternal getTicket(String ticketId);

    TicketInternal updateTicket(UpdateTicketDTO updateTicketDTO);

    InputStream getAttachmentContent(String ticketId, String articleId, String attachmentId);
}

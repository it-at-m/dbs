package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import java.io.InputStream;

public interface TicketingOutPort {
    TicketInternal getTicket(String ticketId);

    InputStream getAttachmentContent(String attachmentId);
}

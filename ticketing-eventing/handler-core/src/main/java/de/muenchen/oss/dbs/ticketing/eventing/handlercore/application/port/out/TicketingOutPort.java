package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTOV2;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import java.io.InputStream;
import java.util.Collection;
import org.springframework.core.io.Resource;

public interface TicketingOutPort {
    TicketInternal getTicket(String ticketId);

    TicketInternal updateTicket(UpdateTicketDTOV2 updateTicketDTO, Collection<Resource> attachments);

    InputStream getAttachmentContent(String ticketId, String articleId, String attachmentId);

    String getTicketHistory(String ticketId);

    User getUserInfo(String userId);
}

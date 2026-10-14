package de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;

import java.util.List;

public interface HtmlGenerationOutPort {
    /**
     * Generate communication html.
     *
     * @param ticket The ticket data for the generation.
     * @param articles The list if articles for the generation.
     * @param customer The customer data for the generation.
     * @param owner The owner data for the generation.
     * @return The communication html as string.
     */
    String generateCommunicationHtml(TicketInternal ticket, List<ArticleInternal> articles, User customer, User owner);
}

package de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.Article;
import de.muenchen.oss.dbs.ticketing.eai.client.model.CreateArticleDTOV2;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTOV2;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;

import java.io.InputStream;
import java.util.Collection;

import org.springframework.core.io.Resource;

public interface TicketingOutPort {

    /**
     * Get ticket by id.
     *
     * @param ticketId The id of the ticket.
     * @return The ticket object.
     */
    TicketInternal getTicket(String ticketId);

    /**
     * Update ticket.
     *
     * @param updateTicketDTO DTO with updated data.
     * @param attachments Attachments to be added.
     * @return The updated ticket object.
     */
    TicketInternal updateTicket(UpdateTicketDTOV2 updateTicketDTO, Collection<Resource> attachments);

    /**
     * Get the ticket history.
     *
     * @param ticketId The id of the ticket.
     * @return The ticket history as string.
     */
    String getTicketHistory(String ticketId);

    /**
     * Create new article for a ticket.
     *
     * @param ticketId The id of the ticket.
     * @param createArticleDTO DTO with data of the new article.
     * @param lhmExtID One of lhmExtID or lhmObjectID can be set.
     * @param lhmObjectID One of lhmExtID or lhmObjectID can be set.
     * @param userid The id of the user.
     * @param attachments Attachments of the article.
     * @return The created article object.
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI") //keeping the same API as in the ticketing-EAI for consistency
    Article createNewArticle(String ticketId, CreateArticleDTOV2 createArticleDTO,
            String lhmExtID, String lhmObjectID, String userid,
            Collection<Resource> attachments);

    /**
     * Get content of attachment.
     *
     * @param ticketId The id of the ticket to which the attachment belongs.
     * @param articleId The id of the article to which the attachment belongs.
     * @param attachmentId The id of the attachment.
     * @return The content of the attachment as InputStream.
     */
    InputStream getAttachmentContent(String ticketId, String articleId, String attachmentId);

    /**
     * Get the user info.
     *
     * @param userId The id of the user.
     * @return The User object.
     */
    User getUserInfo(String userId);
}

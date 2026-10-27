package de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out;

import de.muenchen.oss.dbs.ticketing.eai.client.model.User;

public interface TicketInfoOutPort {
    /**
     * Get the ticket history.
     *
     * @param ticketId The id of the ticket.
     * @return The ticket history as string.
     */
    String getTicketHistory(String ticketId);

    /**
     * Get the user info.
     *
     * @param userId The id of the user.
     * @return The User object.
     */
    User getUserInfo(String userId);
}

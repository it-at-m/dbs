package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.ticketing;

import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.UsersApi;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.TicketInfoOutPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZammadAdapter implements TicketInfoOutPort {
    private final TicketsApi ticketsApi;
    private final UsersApi usersApi;

    @Override
    public String getTicketHistory(final String ticketId) {
        try {
            return ticketsApi.getTicketHistory(ticketId).block();
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting ticket history with id %s failed: %s"
                    .formatted(ticketId, e.getResponseBodyAsString()), e);
        }
    }

    @Override
    public User getUserInfo(final String userId) {
        try {
            return usersApi.getUserByUserId(userId).block();
        } catch (final WebClientResponseException e) {
            throw new ZammadApiException("Getting user with id %s failed: %s"
                    .formatted(userId, e.getResponseBodyAsString()), e);
        }
    }
}

package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.ticketing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.oss.dbs.ticketing.eai.client.api.TicketsApi;
import de.muenchen.oss.dbs.ticketing.eai.client.api.UsersApi;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ZammadAdapterTest {

    @Mock
    private TicketsApi ticketsApi;

    @Mock
    private UsersApi usersApi;

    @InjectMocks
    private ZammadAdapter zammadAdapter;

    @Test
    void givenTicketId_thenReturnTicketHistory() {
        // Given
        final String ticketId = "ticket_id";
        final String ticketHistory = "Ticket history data";

        when(ticketsApi.getTicketHistory(ticketId)).thenReturn(Mono.just(ticketHistory));

        // When
        final String result = zammadAdapter.getTicketHistory(ticketId);

        // Then
        assertThat(result).isEqualTo(ticketHistory);
        verify(ticketsApi).getTicketHistory(ticketId);
    }

    @Test
    void givenTicketId_thenThrowZammadApiException() {
        //Given
        final String ticketId = "ticket_id";

        when(ticketsApi.getTicketHistory(ticketId)).thenThrow(new WebClientResponseException(500, "Error", null, null, null));

        // When
        final Exception exception = Assertions.assertThrows(ZammadApiException.class,
                () -> zammadAdapter.getTicketHistory(ticketId));

        //Then
        verify(ticketsApi).getTicketHistory(ticketId);
        Assertions.assertEquals(ZammadApiException.class, exception.getClass());
        Assertions.assertEquals(exception.getMessage(), String.format("Getting ticket history with id %s failed: %s", ticketId, ""));
    }

    @Test
    void givenUserId_thenReturnUser() {
        // Given
        final String userId = "user_id";
        final String firstName = "Firstname";
        final String lastName = "Lastname";
        final User user = new User();

        user.setId(userId);
        user.setFirstname(firstName);
        user.setLastname(lastName);

        when(usersApi.getUserByUserId(userId)).thenReturn(Mono.just(user));

        // When
        final User result = zammadAdapter.getUserInfo(userId);

        // Then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getFirstname()).isEqualTo(firstName);
        assertThat(result.getLastname()).isEqualTo(lastName);
        verify(usersApi).getUserByUserId(userId);
    }

    @Test
    void givenUserId_thenThrowZammadApiException() {
        //Given
        final String userId = "user_id";

        when(usersApi.getUserByUserId(userId)).thenThrow(new WebClientResponseException(500, "Error", null, null, null));

        // When
        final Exception exception = Assertions.assertThrows(ZammadApiException.class,
                () -> zammadAdapter.getUserInfo(userId));

        //Then
        verify(usersApi).getUserByUserId(userId);
        Assertions.assertEquals(ZammadApiException.class, exception.getClass());
        Assertions.assertEquals(exception.getMessage(), String.format("Getting user with id %s failed: %s", userId, ""));
    }

}

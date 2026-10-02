package de.muenchen.dbs.ticketing.dprs.api;

import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.TicketsApi;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.UsersApi;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.model.TicketInternal;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.model.UpdateTicketDTO;
import de.muenchen.dbs.ticketing.dprs.clients.ticketingeai.model.User;
import de.muenchen.dbs.ticketing.dprs.mail.EmailService;
import de.muenchen.dbs.ticketing.dprs.util.ResetKeyGenerator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@RequiredArgsConstructor
public class PrsService {

    @NonNull
    private final TicketsApi ticketsApi;
    @NonNull
    private final UsersApi usersApi;
    @NonNull
    private final EmailService emailService;
    @NonNull
    private final ResetKeyGenerator resetKeyGenerator;

    @Value("${prs.link.validityInMinutes}")
    private int linkValidityInMinutes;

    /**
     * Tries to find the Ticket with the given ticketNumber and send a mail to the owner of this ticket.
     * The Mail consists of a time restricted link which contains a encrypted resetKey.
     * This key is checked when the ticket-owner clicks on the link by {@link #getPassword(String)}
     *
     * @param ticketNumber Ticket-Number of the ticket for which to send the link to.
     * @return True, if the operation was successfull, false otherwise.
     */
    public Mono<Boolean> sendPasswordResetMailByTicketNumber(String ticketNumber) {
        return ticketsApi.getTicketByNumberWithUser(ticketNumber, null, null, null)
                .flatMap(ticketInternal -> {
                    // if the ticket already has a DK set, send mail instantly
                    if (!StringUtils.isBlank(ticketInternal.getDirektkennwort())) {
                        return usersApi.getUserByUserId(ticketInternal.getCustomerId())
                                .map(sendPasswordResetMailToUserFunction(ticketNumber, ticketInternal));
                        // if the ticket has no DK set, generate it
                    } else {
                        UpdateTicketDTO updateTicketDTO = new UpdateTicketDTO();
                        updateTicketDTO.setId(ticketInternal.getId());

                        String uuidString = UUID.randomUUID().toString();
                        String dk = uuidString.substring(uuidString.lastIndexOf("-") + 1);
                        updateTicketDTO.setDirektkennwort(dk);

                        return ticketsApi.updateTicket(ticketInternal.getId(), updateTicketDTO, null, null)
                                .flatMap(updatedTicket -> usersApi.getUserByUserId(updatedTicket.getCustomerId())
                                        .map(sendPasswordResetMailToUserFunction(ticketNumber, ticketInternal)));
                    }
                });
    }

    private Function<User, Boolean> sendPasswordResetMailToUserFunction(String ticketNumber, TicketInternal ticketInternal) {
        return user -> {
            try {
                String customerMail = user.getEmail();
                String ticketTitle = ticketInternal.getMailTitle();
                if (ticketTitle == null || ticketTitle.isEmpty()) {
                    ticketTitle = ticketInternal.getTitle();
                }
                String resetKey = resetKeyGenerator.generateResetKey(ticketNumber,
                        LocalDateTime.now().plus(linkValidityInMinutes, ChronoUnit.MINUTES));

                emailService.sendPRMessage(customerMail, ticketTitle, resetKey);
                return true;
            } catch (Exception e) {
                log.error("Error sending password reset mail", e);
                return false;
            }
        };
    }

    public Mono<Boolean> sendPasswordResetMailByResetKey(String resetKey) {
        try {
            return sendPasswordResetMailByTicketNumber(resetKeyGenerator.getTicketNumberFromResetKey(resetKey));
        } catch (IllegalArgumentException exception) {
            return Mono.just(false);
        }
    }

    /**
     * Try to receive the direct-password of a ticket with the resetKey a User got sent by Mail.
     * If
     * - the resetKey can be decrypted
     * - the resetKey is still valid
     * - the ticket with the ticketNumber contained in the resetKey can be found
     * this function returns the direct-password of the ticket. Else the optional will be empty.
     *
     * @param resetKey The key the user got sent by mail from
     *            {@link #sendPasswordResetMailByTicketNumber(String)}
     * @return The direct-password to access his ticket, if the above stated rules are fulfilled.
     */
    public Mono<Optional<PasswordDTO>> getPassword(String resetKey) {
        LocalDateTime validUntilDate = resetKeyGenerator.getValidUntilDate(resetKey);
        if (validUntilDate != null) {
            try {
                String ticketNumber = resetKeyGenerator.getTicketNumberFromResetKey(resetKey);

                if (validUntilDate.isAfter(LocalDateTime.now())) {
                    // If the link is still valid, we add the password to the response
                    Mono<TicketInternal> ticketByNumberWithUser = ticketsApi.getTicketByNumberWithUser(ticketNumber, null, null, null);
                    return ticketByNumberWithUser.map(ticketInternal -> Optional.of(new PasswordDTO(ticketInternal.getDirektkennwort(), validUntilDate)));
                } else {
                    // else we will simply send the validUntil state
                    return Mono.just(Optional.of(new PasswordDTO("", validUntilDate)));
                }
            } catch (Exception e) {
                log.warn("Unable to retrieve password from reset key", e);
                return Mono.just(Optional.empty());
            }
        } else {
            return Mono.just(Optional.empty());
        }
    }
}

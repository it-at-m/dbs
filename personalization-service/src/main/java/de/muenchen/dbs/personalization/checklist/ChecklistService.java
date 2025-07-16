package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.common.NotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;

    public Checklist createChecklist(final Checklist checklist) {
        final String userMail = getUserMailFromAuthenticationOrThrow();
        log.debug("Create Checklist {} for {}", checklist, userMail);
        checklist.setEmail(userMail);
        return checklistRepository.save(checklist);
    }

    public List<Checklist> getChecklists() {
        final String userMail = getUserMailFromAuthenticationOrThrow();
        log.debug("Get all checklists of {}", userMail);
        return checklistRepository.findChecklistByEmail(userMail);
    }

    public Checklist getChecklist(final UUID checklistId) {
        final String userMail = getUserMailFromAuthenticationOrThrow();
        log.debug("Get checklist with ID {} for {}", checklistId, userMail);
        final Checklist checklistOrThrowException = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(checklistOrThrowException, userMail);

        return checklistOrThrowException;
    }

    public Checklist updateChecklist(final Checklist checklist, final UUID checklistId) {
        final String userMail = getUserMailFromAuthenticationOrThrow();
        log.debug("Update checklist with ID {} for {}", checklistId, userMail);
        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(foundChecklist, userMail);

        foundChecklist.setChecklistItems(checklist.getChecklistItems());
        foundChecklist.setLastUpdate(ZonedDateTime.now());
        log.debug("Update Checklist {}", foundChecklist);
        return checklistRepository.save(foundChecklist);
    }

    public void deleteChecklist(final UUID checklistId) {
        final String userMail = getUserMailFromAuthenticationOrThrow();
        log.debug("Delete Checklist with ID {} for {}", checklistId, userMail);

        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);
        isChecklistOwnerOrThrow(foundChecklist, userMail);

        checklistRepository.deleteById(checklistId);
    }

    private Checklist getChecklistOrThrowException(final UUID checklistId) {
        return checklistRepository
                .findById(checklistId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, checklistId)));
    }

    private String getUserMailFromAuthenticationOrThrow() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object emailClaim = jwt.getClaims().get("email");
            if(emailClaim != null) {
                final String email = emailClaim.toString();
                if (!StringUtils.isBlank(email)) {
                    return email;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private void isChecklistOwnerOrThrow(final Checklist checklist, final String userMail) {
        if (!checklist.getEmail().equals(userMail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not own the checklist");
        }
    }
}

package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItem;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItemServiceNavigatorDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistServiceNavigatorReadDTO;
import de.muenchen.dbs.personalization.common.NotFoundException;
import de.muenchen.dbs.personalization.servicenavigator.ServiceNavigatorService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
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

    private static final String JWT_CLAIM_LHM_EXT_ID = "lhmExtID";
    private final ChecklistRepository checklistRepository;
    private final ServiceNavigatorService serviceNavigatorService;
    private final ChecklistMapper checklistMapper;

    public Checklist createChecklist(final Checklist checklist) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Create Checklist {} for {}", checklist, lhmExtId);
        checklist.setLhmExtId(lhmExtId);
        checklist.setLastUpdate(ZonedDateTime.now());
        return checklistRepository.save(checklist);
    }

    public List<Checklist> getChecklists() {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Get all checklists of {}", lhmExtId);
        return checklistRepository.findChecklistByLhmExtIdOrderByLastUpdateDesc(lhmExtId);
    }

    public ChecklistServiceNavigatorReadDTO getChecklist(final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Get checklist with ID {} for {}", checklistId, lhmExtId);
        final Checklist checklistOrThrowException = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(checklistOrThrowException, lhmExtId);

        return populateWithServiceNavigator(checklistOrThrowException);
    }

    public ChecklistServiceNavigatorReadDTO updateChecklist(final Checklist checklist, final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Update checklist with ID {} for {}", checklistId, lhmExtId);
        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(foundChecklist, lhmExtId);

        foundChecklist.setChecklistItems(checklist.getChecklistItems());
        foundChecklist.setLastUpdate(ZonedDateTime.now());
        log.debug("Update Checklist {}", foundChecklist);

        final Checklist savedChecklist = checklistRepository.save(foundChecklist);

        return populateWithServiceNavigator(savedChecklist);
    }

    public void deleteChecklist(final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Delete Checklist with ID {} for {}", checklistId, lhmExtId);

        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);
        isChecklistOwnerOrThrow(foundChecklist, lhmExtId);

        checklistRepository.deleteById(checklistId);
    }

    public ChecklistServiceNavigatorReadDTO changeChecklistEntry(final UUID checklistId, final String serviceId, final ZonedDateTime newCheckedValue) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        final String sanitizedServiceId = StringEscapeUtils.escapeHtml4(serviceId);
        log.debug("Update checklist with checklist-ID {} and service-ID {} for {}", checklistId, sanitizedServiceId, lhmExtId);
        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(foundChecklist, lhmExtId);

        List<ChecklistItem> checklistItems = foundChecklist.getChecklistItems();
        ChecklistItem updatedChecklistItem = null;

        for (final ChecklistItem checklistItem : checklistItems) {
            if (checklistItem.getServiceID().equals(sanitizedServiceId)) {
                checklistItem.setChecked(newCheckedValue);
                updatedChecklistItem = checklistItem;
                checklistItems.remove(checklistItem);
                break;
            }
        }
        if (updatedChecklistItem != null) {
            checklistItems.addFirst(updatedChecklistItem);
        }

        // do it only for hibernate...https://stackoverflow.com/questions/8773311/unique-constraint-violation-with-ordered-hibernate-list
        checklistItems = new ArrayList<>(checklistItems);
        foundChecklist.setChecklistItems(checklistItems);

        foundChecklist.setLastUpdate(ZonedDateTime.now());

        log.debug("Update Checklist {}", foundChecklist);

        return populateWithServiceNavigator(checklistRepository.save(foundChecklist));
    }

    private ChecklistServiceNavigatorReadDTO populateWithServiceNavigator(final Checklist checklist) {
        final List<ChecklistItemServiceNavigatorDTO> checklistItemDtos = checklist.getChecklistItems().stream()
                .map(checklistItem -> serviceNavigatorService.getServiceNavigatorService(checklistItem.getServiceID()))
                .flatMap(Optional::stream)
                .map(snResponse -> {
                    final ChecklistItemServiceNavigatorDTO newDto = serviceNavigatorService.toDto(snResponse);
                    newDto.setChecked(
                            checklist.getChecklistItems().stream()
                                    .filter(checklistItem -> snResponse.id().equals(checklistItem.getServiceID()))
                                    .findFirst()
                                    .orElseGet(() -> new ChecklistItem(null, null, null, null, null))
                                    .getChecked());
                    return newDto;
                })
                .toList();

        final ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO = checklistMapper.toServiceNavigatorReadDTO(checklist);
        checklistServiceNavigatorReadDTO.setChecklistItemServiceNavigatorDtos(checklistItemDtos);
        return checklistServiceNavigatorReadDTO;
    }

    private Checklist getChecklistOrThrowException(final UUID checklistId) {
        return checklistRepository
                .findById(checklistId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, checklistId)));
    }

    private String getLhmExtIdFromAuthenticationOrThrow() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            final Object lhmExtIdClaim = jwt.getClaims().get(JWT_CLAIM_LHM_EXT_ID);
            if (lhmExtIdClaim != null && !StringUtils.isBlank(lhmExtIdClaim.toString())) {
                return lhmExtIdClaim.toString();
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private void isChecklistOwnerOrThrow(final Checklist checklist, final String lhmExtId) {
        if (!checklist.getLhmExtId().equals(lhmExtId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not own the checklist");
        }
    }
}

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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final ChecklistMapper checklistMapper;
    private final ServiceNavigatorService serviceNavigatorService;

    public Checklist createChecklist(final Checklist checklist) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Create Checklist {} for {}", checklist, lhmExtId);
        checklist.setLhmExtId(lhmExtId);
        return checklistRepository.save(checklist);
    }

    public List<Checklist> getChecklists() {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Get all checklists of {}", lhmExtId);
        return checklistRepository.findChecklistByLhmExtId(lhmExtId);
    }

    public ChecklistServiceNavigatorReadDTO getChecklist(final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Get checklist with ID {} for {}", checklistId, lhmExtId);
        final Checklist checklistOrThrowException = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(checklistOrThrowException, lhmExtId);

        final String serviceIds = checklistOrThrowException.getChecklistItems().stream()
                .map(ChecklistItem::getServiceID)
                .collect(Collectors.joining(","));

        final List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDtos = serviceNavigatorService
                .getChecklistItemServiceNavigatorDTO(serviceIds);
        final ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO = checklistMapper.toServiceNavigatorReadDTO(checklistOrThrowException);
        checklistServiceNavigatorReadDTO.setChecklistItemServiceNavigatorDtos(checklistItemServiceNavigatorDtos);

        return checklistServiceNavigatorReadDTO;
    }

    public ChecklistServiceNavigatorReadDTO updateChecklist(final Checklist checklist, final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Update checklist with ID {} for {}", checklistId, lhmExtId);
        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);

        isChecklistOwnerOrThrow(foundChecklist, lhmExtId);

        foundChecklist.setChecklistItems(checklist.getChecklistItems());
        foundChecklist.setLastUpdate(ZonedDateTime.now());
        log.debug("Update Checklist {}", foundChecklist);

        Checklist savedChecklist = checklistRepository.save(foundChecklist);
        String serviceIds = savedChecklist.getChecklistItems().stream()
                .map(ChecklistItem::getServiceID)
                .collect(Collectors.joining(","));

        final List<ChecklistItemServiceNavigatorDTO> checklistItemServiceNavigatorDtos = serviceNavigatorService
                .getChecklistItemServiceNavigatorDTO(serviceIds);
        final ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO = checklistMapper.toServiceNavigatorReadDTO(savedChecklist);
        checklistServiceNavigatorReadDTO.setChecklistItemServiceNavigatorDtos(checklistItemServiceNavigatorDtos);

        return checklistServiceNavigatorReadDTO;
    }

    public void deleteChecklist(final UUID checklistId) {
        final String lhmExtId = getLhmExtIdFromAuthenticationOrThrow();
        log.debug("Delete Checklist with ID {} for {}", checklistId, lhmExtId);

        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);
        isChecklistOwnerOrThrow(foundChecklist, lhmExtId);

        checklistRepository.deleteById(checklistId);
    }

    private Checklist getChecklistOrThrowException(final UUID checklistId) {
        return checklistRepository
                .findById(checklistId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, checklistId)));
    }

    private String getLhmExtIdFromAuthenticationOrThrow() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            final Object lhmExtIdClaim = jwt.getClaims().get("lhmExtId");
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

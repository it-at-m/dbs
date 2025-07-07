package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.common.NotFoundException;
import de.muenchen.dbs.personalization.security.Authorities;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;

    @PreAuthorize(Authorities.CHECKLIST_CREATE)
    public Checklist createChecklist(final Checklist checklist) {
        log.debug("Create Checklist {}", checklist);
        return checklistRepository.save(checklist);
    }

    @PreAuthorize(Authorities.CHECKLIST_GET_ALL)
    public List<Checklist> getChecklists(final String userId) {
        log.debug("Get all checklists of {}", userId);
        return checklistRepository.findChecklistByLhmExtId(userId);
    }

    @PreAuthorize(Authorities.CHECKLIST_GET)
    public Checklist getChecklist(final UUID checklistId) {
        log.debug("Get checklist with ID {}", checklistId);
        return getChecklistOrThrowException(checklistId);
    }

    @PreAuthorize(Authorities.CHECKLIST_UPDATE)
    public Checklist updateChecklist(final Checklist checklist, final UUID checklistId) {
        final Checklist foundChecklist = getChecklistOrThrowException(checklistId);
        foundChecklist.setChecklistItems(checklist.getChecklistItems());
        foundChecklist.setLastUpdate(ZonedDateTime.now());
        log.debug("Update Checklist {}", foundChecklist);
        return checklistRepository.save(foundChecklist);
    }

    @PreAuthorize(Authorities.CHECKLIST_DELETE)
    public void deleteChecklist(final UUID checklistId) {
        log.debug("Delete Checklist with ID {}", checklistId);
        checklistRepository.deleteById(checklistId);
    }

    private Checklist getChecklistOrThrowException(final UUID checklistId) {
        return checklistRepository
                .findById(checklistId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, checklistId)));
    }
}

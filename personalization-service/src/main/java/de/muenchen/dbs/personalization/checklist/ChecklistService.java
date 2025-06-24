package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.common.ExceptionMessageConstants.MSG_NOT_FOUND;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.common.NotFoundException;
import de.muenchen.dbs.personalization.security.Authorities;
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
    public Checklist createChecklist(final String userId, final List<String> serviceIds) {
        log.debug("Create Checklist for {} with {}}", userId, serviceIds);
        Checklist createdChecklist = new Checklist();
        // Get Services from ServiceApi
        return checklistRepository.save(createdChecklist);
    }

    @PreAuthorize(Authorities.CHECKLIST_GET_ALL)
    public List<Checklist> getChecklists(final String userId) {
        log.info("Get all checklists of {}", userId);
        return checklistRepository.findChecklistByLhmExtId(userId);
    }

    @PreAuthorize(Authorities.CHECKLIST_GET)
    public Checklist getChecklist(final UUID checklistId) {
        log.info("Get checklist with ID {}", checklistId);
        return getCheckistOrThrowException(checklistId);
    }

    @PreAuthorize(Authorities.CHECKLIST_UPDATE)
    public Checklist updateChecklist(final Checklist checklist, final UUID checklistId) {
        final Checklist foundChecklist = getCheckistOrThrowException(checklistId);
        foundChecklist.setChecklistItems(checklist.getChecklistItems());
        log.debug("Update Checklist {}", foundChecklist);
        return checklistRepository.save(foundChecklist);
    }

    private Checklist getCheckistOrThrowException(final UUID checklistId) {
        return checklistRepository
                .findById(checklistId)
                .orElseThrow(() -> new NotFoundException(String.format(MSG_NOT_FOUND, checklistId)));
    }
}

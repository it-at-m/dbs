package de.muenchen.dbs.personalization.checklist;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistCreateDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistReadDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checklist")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Checklists", description = "Creating, reading and deleting Checklists.")
public class ChecklistController {

    private final ChecklistService checklistService;
    private final ChecklistMapper checklistMapper;

    @GetMapping
    @Operation(summary = "Get all checklists by user.", description = "Returns all checklists of an user (identified by JWT-Token)")
    @ResponseStatus(HttpStatus.OK)
    public List<ChecklistReadDTO> getChecklists() {
        final List<Checklist> checklists = checklistService.getChecklists();
        return checklists.stream().map(checklistMapper::toReadDTO).toList();
    }

    @GetMapping(path = "/{checklistID}")
    @Operation(summary = "Get specific checklist by checklist-id.", description = "Returns a checklist by checklistId")
    @ResponseStatus(HttpStatus.OK)
    public ChecklistReadDTO getChecklist(@PathVariable("checklistID") final UUID checklistID) {
        return checklistMapper.toReadDTO(checklistService.getChecklist(checklistID));
    }

    @PostMapping
    @Operation(summary = "Create a new checklist", description = "Creates a new checklist using the provided checklist details.")
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistReadDTO createChecklist(@Valid @RequestBody final ChecklistCreateDTO checklistCreateDTO) {
        return checklistMapper
                .toReadDTO(checklistService.createChecklist(checklistMapper.toCreateChecklist(checklistCreateDTO)));
    }

    @PutMapping("/{checklistID}")
    @Operation(summary = "Update a checklist", description = "Updates a checklist using the provided checklist details.")
    @ResponseStatus(HttpStatus.OK)
    public ChecklistReadDTO updateChecklist(@Valid @RequestBody final ChecklistUpdateDTO checklistUpdateDTO,
            @PathVariable("checklistID") final UUID checklistID) {
        return checklistMapper.toReadDTO(checklistService.updateChecklist(checklistMapper.toUpdateChecklist(checklistUpdateDTO), checklistID));
    }

    @DeleteMapping("/{checklistID}")
    @Operation(summary = "Delete a checklist", description = "Deletes a checklist by checklistId.")
    @ResponseStatus(HttpStatus.OK)
    public void deleteChecklist(@PathVariable("checklistID") final UUID checklistID) {
        checklistService.deleteChecklist(checklistID);
    }
}

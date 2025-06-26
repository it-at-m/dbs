package de.muenchen.dbs.personalization.checklist;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistCreateDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistReadDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistUpdateDTO;
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
public class ChecklistController {

    private final ChecklistService checklistService;
    private final ChecklistMapper checklistMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChecklistReadDTO> getChecklists(@RequestHeader("lhmExtID") final String lhmExtID) {
        final List<Checklist> checklists = checklistService.getChecklists(lhmExtID);
        return checklists.stream().map(checklistMapper::toReadDTO).toList();
    }

    @GetMapping(path = "/{checklistID}")
    @ResponseStatus(HttpStatus.OK)
    public ChecklistReadDTO getChecklist(@PathVariable("checklistID") final UUID checklistID) {
        return checklistMapper.toReadDTO(checklistService.getChecklist(checklistID));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistReadDTO createChecklist(@Valid @RequestBody final ChecklistCreateDTO checklistCreateDTO) {
        return checklistMapper.toReadDTO(checklistService.createChecklist(checklistCreateDTO.lhmExtId(), checklistCreateDTO.checklistItems()));
    }

    @PutMapping("/{checklistID}")
    @ResponseStatus(HttpStatus.OK)
    public ChecklistReadDTO updateChecklist(@Valid @RequestBody final ChecklistUpdateDTO checklistUpdateDTO,
            @PathVariable("checklistID") final UUID checklistID) {
        return checklistMapper.toReadDTO(checklistService.updateChecklist(checklistMapper.toUpdateChecklist(checklistUpdateDTO), checklistID));
    }
}

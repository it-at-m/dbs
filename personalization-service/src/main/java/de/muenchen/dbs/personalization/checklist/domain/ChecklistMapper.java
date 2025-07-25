package de.muenchen.dbs.personalization.checklist.domain;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChecklistMapper {

    ChecklistReadDTO toReadDTO(Checklist checklist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    Checklist toCreateChecklist(ChecklistCreateDTO checklistUpdateDTO);

    @Mapping(target = "id", ignore = true)
    Checklist toUpdateChecklist(ChecklistUpdateDTO checklistUpdateDTO);

    List<ChecklistItemDTO> toChecklistItemDTOList(List<ChecklistItem> checklistItems);

    List<ChecklistItem> toChecklistItemList(List<ChecklistItemDTO> checklistItemDTO);

}

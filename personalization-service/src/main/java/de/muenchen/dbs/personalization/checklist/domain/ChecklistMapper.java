package de.muenchen.dbs.personalization.checklist.domain;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChecklistMapper {

    ChecklistReadDTO toReadDTO(Checklist checklist);

    @Mapping(target = "checklistItemServiceNavigatorDtos", ignore = true)
    ChecklistServiceNavigatorReadDTO toServiceNavigatorReadDTO(Checklist checklist);

    @Mapping(target = "checklistItems", ignore = true)
    @Mapping(target = "id", ignore = true)
    Checklist toChecklist(ChecklistServiceNavigatorReadDTO checklistServiceNavigatorReadDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lhmExtId", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    Checklist toCreateChecklist(ChecklistCreateDTO checklistUpdateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    Checklist toUpdateChecklist(ChecklistUpdateDTO checklistUpdateDTO);

    List<ChecklistItemDTO> toChecklistItemDTOList(List<ChecklistItem> checklistItems);

    List<ChecklistItem> toChecklistItemList(List<ChecklistItemDTO> checklistItemDTO);

}

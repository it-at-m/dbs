package de.muenchen.dbs.personalization.checklist.domain;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChecklistMapper {

    ChecklistReadDTO toReadDTO(Checklist checklist);

    @Mapping(target = "id", ignore = true)
    Checklist toUpdateChecklist(ChecklistUpdateDTO checklistUpdateDTO);
}

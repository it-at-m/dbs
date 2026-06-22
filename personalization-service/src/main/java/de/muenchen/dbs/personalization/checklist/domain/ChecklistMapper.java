package de.muenchen.dbs.personalization.checklist.domain;

import de.muenchen.dbs.personalization.servicenavigator.OnlineService;
import de.muenchen.dbs.personalization.servicenavigator.ServiceNavigatorResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChecklistMapper {

    ChecklistReadDTO toReadDTO(Checklist checklist);

    @Mapping(target = "checklistItemServiceNavigatorDtos", ignore = true)
    ChecklistServiceNavigatorReadDTO toServiceNavigatorReadDTO(Checklist checklist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lhmExtId", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    Checklist toCreateChecklist(ChecklistCreateDTO checklistUpdateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    Checklist toUpdateChecklist(ChecklistUpdateDTO checklistUpdateDTO);

    List<ChecklistItemDTO> toChecklistItemDTOList(List<ChecklistItem> checklistItems);

    List<ChecklistItem> toChecklistItemList(List<ChecklistItemDTO> checklistItemDTO);

    // Element mapping for ServiceNavigator OnlineService -> DTO
    OnlineServiceDTO toOnlineServiceDTO(OnlineService source);

    // Ensure null list maps to empty list for online services
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<OnlineServiceDTO> toOnlineServiceDTOs(List<OnlineService> source);

    // Mapping from ServiceNavigatorResponse -> ChecklistItemServiceNavigatorDTO
    @Mapping(source = "serviceName", target = "title")
    @Mapping(source = "summary", target = "note")
    @Mapping(source = "id", target = "serviceID")
    @Mapping(source = "mandatory", target = "required")
    ChecklistItemServiceNavigatorDTO toChecklistItemServiceNavigatorDTO(ServiceNavigatorResponse source);
}

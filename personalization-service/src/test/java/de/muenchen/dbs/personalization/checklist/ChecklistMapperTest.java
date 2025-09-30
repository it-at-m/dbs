package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.checklist.ChecklistTestHelper.createTestChecklist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.dbs.personalization.checklist.domain.*;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@AllArgsConstructor
public class ChecklistMapperTest {

    private final ChecklistMapper checklistMapper = Mappers.getMapper(ChecklistMapper.class);

    @Nested
    class ToReadDTO {
        @Test
        void givenChecklist_thenReturnsCorrectDTO() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, "user@example.com", null);

            // When
            final ChecklistReadDTO result = checklistMapper.toReadDTO(checklist);

            // Then
            assertNotNull(result);
            assertThat(result).usingRecursiveComparison().ignoringFields("checklistItems").isEqualTo(checklist);
            assertEquals(result.checklistItems(), checklistMapper.toChecklistItemDTOList(checklist.getChecklistItems()));
        }
    }

    @Nested
    class ToCreateChecklist {
        @Test
        void givenRequestDTO_thenReturnsCorrectEntity() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, "user-lhm-ext-id", null);
            final ChecklistCreateDTO checklistCreateDTO = new ChecklistCreateDTO(checklist.getTitle(),
                    "situation-id-sample",
                    checklistMapper.toChecklistItemDTOList(checklist.getChecklistItems()));

            // When
            final Checklist result = checklistMapper.toCreateChecklist(checklistCreateDTO);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("id", "lhmExtId", "lastUpdate").isEqualTo(checklistCreateDTO);
        }
    }

    @Nested
    class ToUpdateChecklist {
        @Test
        void givenRequestDTO_thenReturnsCorrectEntity() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, "user@example.com", null);
            final ChecklistUpdateDTO checklistUpdateDTO = new ChecklistUpdateDTO(id, checklist.getLhmExtId(), checklist.getTitle(),
                    checklistMapper.toChecklistItemDTOList(checklist.getChecklistItems()));

            // When
            final Checklist result = checklistMapper.toUpdateChecklist(checklistUpdateDTO);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("id", "situationId", "lastUpdate").isEqualTo(checklistUpdateDTO);
        }
    }

    @Nested
    class ToServiceNavigatorReadDTO {
        @Test
        void givenChecklist_thenReturnsCorrectEntity() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, "user@example.com", null);
            checklist.setSituationId("situation_id");
            final ChecklistServiceNavigatorReadDTO result = checklistMapper.toServiceNavigatorReadDTO(checklist);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("checklistItemServiceNavigatorDtos").isEqualTo(checklist);
        }
    }
}

package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.checklist.ChecklistTestHelper.createTestChecklist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.common.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChecklistServiceTest {

    private static final String USER_ID = "userId";

    @Mock
    private ChecklistRepository checklistRepository;

    @InjectMocks
    private ChecklistService checklistService;

    //     TODO Completion after adding the services interface
    //
    @Nested
    class CreateChecklist {

        @Test
        void givenChecklist_thenReturnChecklist() {
            // Given
            final List<String> listOfIds = List.of("item1", "item2");

            final Checklist checklistToSave = createTestChecklist(null, USER_ID, null);
            final Checklist expectedChecklist = createTestChecklist(UUID.randomUUID(), checklistToSave.getLhmExtId(), null);

            when(checklistRepository.save(argThat(checklist -> checklist.getLhmExtId().equals(checklistToSave.getLhmExtId()) &&
                    checklist.getTitle().equals(checklistToSave.getTitle()) &&
                    checklist.getChecklistItems().equals(checklistToSave.getChecklistItems())))).thenReturn(expectedChecklist);

            // When
            final Checklist result = checklistService.createChecklist(USER_ID, "title", listOfIds);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("id", "lastUpdate").isEqualTo(expectedChecklist);
            verify(checklistRepository).save(argThat(checklist -> checklist.getLhmExtId().equals(checklistToSave.getLhmExtId()) &&
                    checklist.getTitle().equals(checklistToSave.getTitle()) &&
                    checklist.getChecklistItems().equals(checklistToSave.getChecklistItems())));
        }
    }

    @Nested
    class GetChecklists {

        @Test
        void givenLhmExtId_thenReturnChecklists() {
            // Given

            final UUID id1 = UUID.randomUUID();
            final UUID id2 = UUID.randomUUID();

            final Checklist checklist1 = createTestChecklist(id1, USER_ID, null);
            final Checklist checklist2 = createTestChecklist(id2, USER_ID, null);

            when(checklistRepository.findChecklistByLhmExtId(USER_ID)).thenReturn(List.of(checklist1, checklist2));

            // When
            final List<Checklist> result = checklistService.getChecklists(USER_ID);

            // Then
            Assertions.assertEquals(List.of(checklist1, checklist2), result);
            verify(checklistRepository, times(1)).findChecklistByLhmExtId(USER_ID);
        }

    }

    @Nested
    class GetChecklist {

        @Test
        void givenUUID_thenReturnChecklist() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, USER_ID, null);

            when(checklistRepository.findById(id)).thenReturn(Optional.of(checklist));

            // When
            final Checklist result = checklistService.getChecklist(id);

            // Then
            verify(checklistRepository).findById(id);
            assertThat(result).usingRecursiveComparison().isEqualTo(checklist);
        }

        @Test
        void givenNonExistentUUID_thenThrowNotFoundException() {
            // Given
            final UUID id = UUID.randomUUID();
            when(checklistRepository.findById(id)).thenReturn(Optional.empty());

            // When
            final Exception exception = Assertions.assertThrows(NotFoundException.class, () -> checklistService.getChecklist(id));

            // Then
            verify(checklistRepository).findById(id);
            Assertions.assertEquals(NotFoundException.class, exception.getClass());
            Assertions.assertEquals(exception.getMessage(), String.format("404 NOT_FOUND \"Could not find entity with id %s\"", id));
        }

    }

    @Nested
    class UpdateChecklist {

        @Test
        void givenChecklist_thenReturnChecklist() {
            // Given
            final UUID checklistToUpdateId = UUID.randomUUID();
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_ID, null);
            final Checklist expectedChecklist = createTestChecklist(null, checklistToUpdate.getLhmExtId(), null);

            when(checklistRepository.save(checklistToUpdate)).thenReturn(expectedChecklist);
            when(checklistRepository.findById(checklistToUpdateId)).thenReturn(Optional.of(checklistToUpdate));

            // When
            final Checklist result = checklistService.updateChecklist(checklistToUpdate, checklistToUpdateId);

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(expectedChecklist);
            verify(checklistRepository).save(checklistToUpdate);
        }

        @Test
        void givenChecklist_thenThrowNotFoundException() {
            // Given
            final UUID checklistToUpdateId = UUID.randomUUID();
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_ID, null);

            when(checklistRepository.findById(checklistToUpdate.getId())).thenReturn(Optional.empty());

            // When
            final Exception exception = Assertions.assertThrows(NotFoundException.class,
                    () -> checklistService.updateChecklist(checklistToUpdate, checklistToUpdateId));

            // Then
            verify(checklistRepository, times(1)).findById(checklistToUpdate.getId());
            Assertions.assertEquals(NotFoundException.class, exception.getClass());
            Assertions.assertEquals(exception.getMessage(), String.format("404 NOT_FOUND \"Could not find entity with id %s\"", checklistToUpdateId));

        }
    }

    @Nested
    class DeleteChecklist {
        @Test
        void givenChecklistId_thenReturnVoid() {
            // Given
            final UUID checklistToDeleteId = UUID.randomUUID();
            doNothing().when(checklistRepository).deleteById(checklistToDeleteId);

            // When
            checklistService.deleteChecklist(checklistToDeleteId);

            // Then
            verify(checklistRepository).deleteById(checklistToDeleteId);
        }
    }
}

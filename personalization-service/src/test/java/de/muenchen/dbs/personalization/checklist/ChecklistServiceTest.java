package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.checklist.ChecklistTestHelper.createTestChecklist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.dbs.personalization.IntegrationTestBase;
import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.common.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
public class ChecklistServiceTest {

    private static final String USER_EMAIL = "user@example.com";

    @Mock
    private ChecklistRepository checklistRepository;

    @InjectMocks
    private ChecklistService checklistService;

    @BeforeEach
    public void setup() {
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                new Jwt("tokenvalue",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256",
                                "typ", "JWT"),
                        Map.of("email", USER_EMAIL))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    class CreateChecklist {

        @Test
        void givenChecklist_thenReturnChecklist() {
            // Given

            final Checklist checklistToSave = createTestChecklist(null, USER_EMAIL, null);
            final Checklist expectedChecklist = createTestChecklist(UUID.randomUUID(), checklistToSave.getEmail(), null);

            when(checklistRepository.save(argThat(checklist -> checklist.getEmail().equals(checklistToSave.getEmail()) &&
                    checklist.getTitle().equals(checklistToSave.getTitle()) &&
                    checklist.getChecklistItems().equals(checklistToSave.getChecklistItems())))).thenReturn(expectedChecklist);

            // When
            final Checklist result = checklistService.createChecklist(checklistToSave);

            // Then
            assertThat(result).usingRecursiveComparison().ignoringFields("id", "lastUpdate").isEqualTo(expectedChecklist);
            verify(checklistRepository).save(argThat(checklist -> checklist.getEmail().equals(checklistToSave.getEmail()) &&
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

            final Checklist checklist1 = createTestChecklist(id1, USER_EMAIL, null);
            final Checklist checklist2 = createTestChecklist(id2, USER_EMAIL, null);

            when(checklistRepository.findChecklistByEmail(USER_EMAIL)).thenReturn(List.of(checklist1, checklist2));

            // When
            final List<Checklist> result = checklistService.getChecklists();

            // Then
            Assertions.assertEquals(List.of(checklist1, checklist2), result);
            verify(checklistRepository, times(1)).findChecklistByEmail(USER_EMAIL);
        }

    }

    @Nested
    class GetChecklist {

        @Test
        void givenUUID_thenReturnChecklist() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, USER_EMAIL, null);

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
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_EMAIL, null);
            final Checklist expectedChecklist = createTestChecklist(null, checklistToUpdate.getEmail(), null);

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
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_EMAIL, null);

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

            final Checklist checklistToUpdate = createTestChecklist(checklistToDeleteId, USER_EMAIL, null);
            when(checklistRepository.findById(checklistToDeleteId)).thenReturn(Optional.of(checklistToUpdate));

            // When
            checklistService.deleteChecklist(checklistToDeleteId);

            // Then
            verify(checklistRepository).deleteById(checklistToDeleteId);
        }
    }
}

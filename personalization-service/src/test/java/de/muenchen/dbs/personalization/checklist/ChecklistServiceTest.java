package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.checklist.ChecklistTestHelper.createTestChecklist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistServiceNavigatorReadDTO;
import de.muenchen.dbs.personalization.common.NotFoundException;
import de.muenchen.dbs.personalization.servicenavigator.ServiceNavigatorService;
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

    private static final String USER_LHM_EXT_ID = "user-lhm-ext-id";

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ServiceNavigatorService serviceNavigatorService;

    @InjectMocks
    private ChecklistService checklistService;

    @BeforeEach
    public void setup() {
        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                new Jwt("tokenvalue",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256",
                                "typ", "JWT"),
                        Map.of("lhmExtID", USER_LHM_EXT_ID)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    class CreateChecklist {

        @Test
        void givenChecklist_thenReturnChecklist() {
            // Given

            final Checklist checklistToSave = createTestChecklist(null, USER_LHM_EXT_ID, null);
            final Checklist expectedChecklist = createTestChecklist(UUID.randomUUID(), checklistToSave.getLhmExtId(), null);

            when(checklistRepository.save(argThat(checklist -> checklist.getLhmExtId().equals(checklistToSave.getLhmExtId()) &&
                    checklist.getTitle().equals(checklistToSave.getTitle()) &&
                    checklist.getChecklistItems().equals(checklistToSave.getChecklistItems())))).thenReturn(expectedChecklist);

            // When
            final Checklist result = checklistService.createChecklist(checklistToSave);

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

            final Checklist checklist1 = createTestChecklist(id1, USER_LHM_EXT_ID, null);
            final Checklist checklist2 = createTestChecklist(id2, USER_LHM_EXT_ID, null);

            when(checklistRepository.findChecklistByLhmExtIdOrderByLastUpdateDesc(USER_LHM_EXT_ID)).thenReturn(List.of(checklist1, checklist2));

            // When
            final List<Checklist> result = checklistService.getChecklists();

            // Then
            Assertions.assertEquals(List.of(checklist1, checklist2), result);
            verify(checklistRepository, times(1)).findChecklistByLhmExtIdOrderByLastUpdateDesc(USER_LHM_EXT_ID);
        }

    }

    @Nested
    class GetChecklist {

        @Test
        void givenUUID_thenReturnChecklist() {
            // Given
            final UUID id = UUID.randomUUID();
            final Checklist checklist = createTestChecklist(id, USER_LHM_EXT_ID, null);

            when(checklistRepository.findById(id)).thenReturn(Optional.of(checklist));
            final ChecklistServiceNavigatorReadDTO expectedChecklistServiceNavigatorReadDTO = new ChecklistServiceNavigatorReadDTO();
            expectedChecklistServiceNavigatorReadDTO.setId(checklist.getId());
            expectedChecklistServiceNavigatorReadDTO.setLhmExtId(checklist.getLhmExtId());
            when(serviceNavigatorService.getChecklistServiceNavigatorReadDTO(checklist)).thenReturn(expectedChecklistServiceNavigatorReadDTO);

            // When
            final ChecklistServiceNavigatorReadDTO result = checklistService.getChecklist(id);

            // Then
            verify(checklistRepository).findById(id);
            assertThat(result).isNotNull();
            assertThat(result).usingRecursiveComparison().isEqualTo(expectedChecklistServiceNavigatorReadDTO);
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
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_LHM_EXT_ID, null);

            final Checklist expectedChecklist = createTestChecklist(null, checklistToUpdate.getLhmExtId(), null);
            final ChecklistServiceNavigatorReadDTO expectedChecklistServiceNavigatorReadDTO = new ChecklistServiceNavigatorReadDTO();
            expectedChecklistServiceNavigatorReadDTO.setId(expectedChecklist.getId());
            expectedChecklistServiceNavigatorReadDTO.setLhmExtId(expectedChecklist.getLhmExtId());
            expectedChecklistServiceNavigatorReadDTO.setTitle(expectedChecklist.getTitle());

            // Mock-Verhalten
            when(checklistRepository.findById(checklistToUpdateId)).thenReturn(Optional.of(checklistToUpdate));
            when(checklistRepository.save(checklistToUpdate)).thenReturn(expectedChecklist);
            when(serviceNavigatorService.getChecklistServiceNavigatorReadDTO(expectedChecklist)).thenReturn(expectedChecklistServiceNavigatorReadDTO);

            // When
            final ChecklistServiceNavigatorReadDTO result = checklistService.updateChecklist(checklistToUpdate, checklistToUpdateId);

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(expectedChecklistServiceNavigatorReadDTO);
            verify(checklistRepository).save(checklistToUpdate);
            verify(checklistRepository).findById(checklistToUpdateId);
        }

        @Test
        void givenChecklist_thenThrowNotFoundException() {
            // Given
            final UUID checklistToUpdateId = UUID.randomUUID();
            final Checklist checklistToUpdate = createTestChecklist(checklistToUpdateId, USER_LHM_EXT_ID, null);

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

            final Checklist checklistToUpdate = createTestChecklist(checklistToDeleteId, USER_LHM_EXT_ID, null);
            when(checklistRepository.findById(checklistToDeleteId)).thenReturn(Optional.of(checklistToUpdate));

            // When
            checklistService.deleteChecklist(checklistToDeleteId);

            // Then
            verify(checklistRepository).deleteById(checklistToDeleteId);
        }
    }
}

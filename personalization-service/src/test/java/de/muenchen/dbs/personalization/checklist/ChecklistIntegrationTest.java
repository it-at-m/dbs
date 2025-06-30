package de.muenchen.dbs.personalization.checklist;

import static de.muenchen.dbs.personalization.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.dbs.personalization.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.dbs.personalization.checklist.ChecklistTestHelper.createTestChecklist;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.dbs.personalization.TestConstants;
import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistCreateDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItem;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistUpdateDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class ChecklistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testChecklistId;

    @Autowired
    private ChecklistRepository checklistRepository;

    @BeforeEach
    public void setUp() {
        final Checklist exampleChecklist = createTestChecklist(null, "lhmExtId", null);
        testChecklistId = checklistRepository.save(exampleChecklist).getId();
    }

    @AfterEach
    public void tearDown() {
        checklistRepository.deleteById(testChecklistId);
    }

    @Nested
    class GetChecklist {
        @Test
        void givenChecklistId_thenReturnChecklist() throws Exception {
            mockMvc.perform(get("/checklist/{checklistID}", testChecklistId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(testChecklistId.toString())));
        }
    }

    @Nested
    class GetChecklists {
        @Test
        void givenLhmExtId_thenReturnChecklists() throws Exception {
            mockMvc.perform(get("/checklist")
                    .header("lhmExtID", "lhmExtId")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    class CreateChecklist {
        @Test
        void givenChecklist_thenChecklistIsSaved() throws Exception {
            final ChecklistCreateDTO requestDTO = new ChecklistCreateDTO("createChecklistId", List.of("item1", "item2"));
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(post("/checklist")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.lhmExtId", is(requestDTO.lhmExtId())));
        }
    }

    @Nested
    class UpdateChecklist {
        @Test
        void givenChecklist_thenChecklistIsUpdated() throws Exception {
            final ChecklistItem checklistItem1 = new ChecklistItem();
            final ChecklistItem checklistItem2 = new ChecklistItem();
            final ChecklistItem checklistItem3 = new ChecklistItem();
            checklistItem1.setServiceID("item1");
            checklistItem2.setServiceID("item2");
            checklistItem3.setServiceID("item3");
            final ChecklistUpdateDTO requestDTO = new ChecklistUpdateDTO(testChecklistId, "lhmExtId", List.of(checklistItem1, checklistItem2, checklistItem3));
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(put("/checklist/{checklistID}", testChecklistId)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(testChecklistId.toString())))
                    .andExpect(jsonPath("$.checklistItems", hasSize(3)));
        }
    }

    @Nested
    class DeleteChecklist {
        @Test
        void givenChecklistId_thenChecklistIsDeleted() throws Exception {
            mockMvc.perform(delete("/checklist/{checklistID}", testChecklistId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

}

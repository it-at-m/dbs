package de.muenchen.dbs.personalization.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.dbs.personalization.IntegrationTestBase;
import de.muenchen.dbs.personalization.TestConstants;
import de.muenchen.dbs.personalization.checklist.ChecklistRepository;
import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistCreateDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistItem;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistReadDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@AutoConfigureMockMvc
class UnicodeConfigurationTest extends IntegrationTestBase {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/checklist";

    private final ChecklistMapper checklistMapper = Mappers.getMapper(ChecklistMapper.class);

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string:
     * String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private ChecklistRepository checklistRepository;

    @Test
    void testForNfcNormalization() throws Exception {
        // Given
        // Persist entity with decomposed string.
        final ChecklistItem checklistItem1 = new ChecklistItem();
        final ChecklistItem checklistItem2 = new ChecklistItem();
        final ChecklistItem checklistItem3 = new ChecklistItem();
        checklistItem1.setServiceID("item1");
        checklistItem2.setServiceID("item2");
        checklistItem3.setServiceID("item3");
        final ChecklistCreateDTO checklistCreateDTO = new ChecklistCreateDTO(TEXT_ATTRIBUTE_DECOMPOSED,
                "situation-id-sample",
                checklistMapper.toChecklistItemDTOList(List.of(checklistItem1, checklistItem2, checklistItem3)));

        // When
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(ENTITY_ENDPOINT_URL)
                .content(objectMapper.writeValueAsString(checklistCreateDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(DEFAULT_JWT)));

        assertThat(response).isNotNull();
        assertThat(response.andExpect(status().isCreated()));

        final ChecklistReadDTO responseChecklist = objectMapper.readValue(response.andReturn().getResponse().getContentAsString(), ChecklistReadDTO.class);

        final Checklist checklist = checklistRepository.findById(responseChecklist.id()).orElse(null);

        // Then
        // Check whether responseChecklist contains a composed string.
        assertNotNull(responseChecklist.lhmExtId());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, responseChecklist.title());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), responseChecklist.title().length());

        // Check persisted entity contains a composed string via JPA repository.
        assertNotNull(checklist.getTitle());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, checklist.getTitle());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), checklist.getTitle().length());
    }

}

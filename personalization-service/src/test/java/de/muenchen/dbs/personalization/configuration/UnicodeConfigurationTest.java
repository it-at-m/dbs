package de.muenchen.dbs.personalization.configuration;

import static de.muenchen.dbs.personalization.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.dbs.personalization.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.dbs.personalization.PersonalizationServiceApplication;
import de.muenchen.dbs.personalization.TestConstants;
import de.muenchen.dbs.personalization.checklist.ChecklistRepository;
import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistCreateDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistReadDTO;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { PersonalizationServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/checklist";

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
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Test
    void testForNfcNormalization() {
        // Given
        // Persist entity with decomposed string.
        final ChecklistCreateDTO checklistCreateDTO = new ChecklistCreateDTO(TEXT_ATTRIBUTE_DECOMPOSED, "title", List.of("item1", "item2"));

        // When
        final ChecklistReadDTO response = testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), checklistCreateDTO, ChecklistReadDTO.class)
                .getBody();

        assert response != null;
        final Checklist checklist = checklistRepository.findById(response.id()).orElse(null);

        // Then
        // Check whether response contains a composed string.
        assertNotNull(response.lhmExtId());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.lhmExtId());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.lhmExtId().length());

        // Check persisted entity contains a composed string via JPA repository.
        assertNotNull(checklist.getLhmExtId());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, checklist.getLhmExtId());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), checklist.getLhmExtId().length());
    }

}

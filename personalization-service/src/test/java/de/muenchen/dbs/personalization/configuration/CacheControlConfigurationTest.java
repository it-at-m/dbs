package de.muenchen.dbs.personalization.configuration;

import static de.muenchen.dbs.personalization.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.dbs.personalization.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.dbs.personalization.PersonalizationServiceApplication;
import de.muenchen.dbs.personalization.TestConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
class CacheControlConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/checklist";

    private static final String EXPECTED_CACHE_CONTROL_HEADER_VALUES = "no-cache, no-store, must-revalidate";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void testForCacheControlHeadersForEntityEndpoint() {
        final ResponseEntity<String> response = testRestTemplate.exchange(ENTITY_ENDPOINT_URL, HttpMethod.GET, null, String.class);
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CACHE_CONTROL));
        assertEquals(EXPECTED_CACHE_CONTROL_HEADER_VALUES, response.getHeaders().getCacheControl());
    }

}

package de.muenchen.dbs.personalization;

import static de.muenchen.dbs.personalization.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.dbs.personalization.TestConstants.SPRING_TEST_PROFILE;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.dbs.personalization.configuration.P13nConfiguration;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        classes = { PersonalizationServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableConfigurationProperties(P13nConfiguration.class)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@AutoConfigureMockMvc
public class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static final String TOKEN_USER_LHM_EXT_ID = "testuser-lhm-ext-id";
    protected static final Jwt DEFAULT_JWT = new Jwt(
            "tokenvalue",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "HS256",
                    "typ", "JWT"),
            Map.of("lhmExtId", TOKEN_USER_LHM_EXT_ID));
}

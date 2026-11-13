/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2023
 */
package de.muenchen.dbs.ticketing.dprs.filter;

import static de.muenchen.dbs.ticketing.dprs.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.dbs.ticketing.dprs.PasswordResetServiceApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { PasswordResetServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(SPRING_TEST_PROFILE)
class CsrfTokenAppendingHelperFilterTest {

    @Autowired
    private WebTestClient webTestClient;

}

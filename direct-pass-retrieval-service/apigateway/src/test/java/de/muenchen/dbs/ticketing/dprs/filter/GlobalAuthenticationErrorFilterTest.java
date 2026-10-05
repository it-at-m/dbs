/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2023
 */
package de.muenchen.dbs.ticketing.dprs.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static de.muenchen.dbs.ticketing.dprs.TestConstants.SPRING_TEST_PROFILE;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import de.muenchen.dbs.ticketing.dprs.PasswordResetServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { PasswordResetServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(SPRING_TEST_PROFILE)
@AutoConfigureWireMock
class GlobalAuthenticationErrorFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        stubFor(get(urlEqualTo("/remote"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeaders(new HttpHeaders(
                                new HttpHeader("Content-Type", "application/json"),
                                new HttpHeader("WWW-Authenticate", "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\""),
                                new HttpHeader("Expires", "Wed, 21 Oct 2099 07:28:06 GMT")))
                        .withBody("{ \"testkey\" : \"testvalue\" }")));

        stubFor(get(urlEqualTo("/remote"))
                .withHeader("dpass", equalTo("test-pass"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())));
    }

    @Disabled("disabled until new endpoints for testing are available")
    @Test
    @WithMockUser
    void backendAuthenticationErrorWhenNoAuth() {
        //@formatter:off
        webTestClient.get().uri("/api/mpdz-ticketing-fragments-backend-service/remote").exchange()
                // due to HttpStatusErrorMapper all 4xx are mapped to "Bad Request"
                .expectStatus().isBadRequest()
                .expectHeader().valueMatches("Content-Type", "application/json")
                .expectHeader().doesNotExist("WWW-Authenticate")
                .expectHeader().valueMatches("Expires", "0")
                .expectBody()
                // due to HttpBodyErrorMapper all 4xx are mapped to "Bad Request"
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("Bad Request");
        //@formatter:on
    }

    @Disabled("disabled until new endpoints for testing are available")
    @Test
    void backendSuccessWithDPass() {
        //@formatter:off
        webTestClient
                .get()
                    .uri("/api/mpdz-ticketing-fragments-backend-service/remote")
                    .header("dpass", "test-pass")
                    .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
        //@formatter:on
    }

}

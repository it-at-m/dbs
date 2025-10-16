package de.muenchen.dbs.personalization.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class KaptaraMocker {

    public static final String API_BASE = "/service/rs/befi/navigator/";

    private KaptaraMocker() {
    }

    public static void setupKaptaraMock() {
        stubFor(any(anyUrl())
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(418)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "error":"Kaptara-Mock Error: No Stub found for this request.",
                                    "error_human":"Endpoint not found"
                                }
                                """)));

        stubFor(
                get(urlPathEqualTo(API_BASE))
                        .withQueryParam("id", matching(".*"))
                        .willReturn(
                                ok()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody("""
                                                {
                                                  "serviceName": "Testservice",
                                                  "publicUrl": "https://example.com",
                                                  "summary": "Lorem Ipsum",
                                                  "onlineServices": [
                                                    {
                                                      "uri": "https://example.com",
                                                      "label": "Online beantragen"
                                                    }
                                                  ],
                                                  "id": "{{ request.query.id }}",
                                                  "isExternal": false
                                                }
                                                """)
                                        .withTransformers("response-template")));
    }

}

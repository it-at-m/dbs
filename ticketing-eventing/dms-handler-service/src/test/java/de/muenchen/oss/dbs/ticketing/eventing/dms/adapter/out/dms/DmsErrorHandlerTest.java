package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.dms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.exception.DmsException;
import de.muenchen.oss.eakte.v1.generated.model.DmsErrorResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientResponseException;

class DmsErrorHandlerTest {

    private final DmsErrorHandler dmsErrorHandler = new DmsErrorHandler(Mappers.getMapper(DmsMapper.class));

    @Test
    void handleError_returnsResult_whenCallSucceeds() {
        final String result = dmsErrorHandler.handleError(() -> "ok");

        assertEquals("ok", result);
    }

    @Test
    void handleError_throwsMappedDmsException_whenWebClientResponseExceptionContainsDmsError() {
        final DmsErrorResponse dmsErrorResponse = new DmsErrorResponse()
                .status(400)
                .text("Bad request")
                .fehlerQuelle(DmsErrorResponse.FehlerQuelleEnum.DMS);
        final RestClientResponseException exception = Mockito.mock(RestClientResponseException.class);
        Mockito.when(exception.getStatusCode()).thenReturn(BAD_REQUEST);
        Mockito.when(exception.getResponseBodyAs(DmsErrorResponse.class)).thenReturn(dmsErrorResponse);

        final DmsException thrown = assertThrows(DmsException.class,
                () -> dmsErrorHandler.handleError(() -> {
                    throw exception;
                }));

        assertEquals(BAD_REQUEST, thrown.getStatus());
        assertNotNull(thrown.getDmsError());
        assertEquals(400, thrown.getDmsError().code());
        assertEquals("Bad request", thrown.getDmsError().message());
        assertEquals("DMS", thrown.getDmsError().source());
        assertSame(exception, thrown.getCause());
    }

    @Test
    void handleError_throwsDmsExceptionWithNullError_whenErrorBodyCannotBeMapped() {
        final RestClientResponseException exception = Mockito.mock(RestClientResponseException.class);
        Mockito.when(exception.getStatusCode()).thenReturn(INTERNAL_SERVER_ERROR);
        Mockito.when(exception.getResponseBodyAs(DmsErrorResponse.class)).thenThrow(new IllegalStateException("invalid body"));

        final DmsException thrown = assertThrows(DmsException.class,
                () -> dmsErrorHandler.handleError(() -> {
                    throw exception;
                }));

        assertEquals(INTERNAL_SERVER_ERROR, thrown.getStatus());
        assertNull(thrown.getDmsError());
        assertSame(exception, thrown.getCause());
    }

    @Test
    void handleError_throwsUnexpectedDmsException_whenNonWebClientExceptionOccurs() {
        final IllegalStateException exception = new IllegalStateException("unexpected");

        final DmsException thrown = assertThrows(DmsException.class,
                () -> dmsErrorHandler.handleError(() -> {
                    throw exception;
                }));

        assertEquals("Dms request failed with unexpected error", thrown.getMessage());
        assertNull(thrown.getStatus());
        assertNull(thrown.getDmsError());
        assertSame(exception, thrown.getCause());
    }
}

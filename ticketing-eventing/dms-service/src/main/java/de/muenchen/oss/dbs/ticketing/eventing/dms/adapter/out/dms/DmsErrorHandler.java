package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.dms;

import de.muenchen.oss.eakte.v1.generated.model.DmsErrorResponse;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.exception.DmsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import java.util.concurrent.Callable;

/**
 * Handles and translates errors from DMS requests.
 */
@Component
@RequiredArgsConstructor
class DmsErrorHandler {
    private static final String DMS_EXCEPTION_MESSAGE = "Dms request failed with status code %s and error response: %s";

    private final DmsMapper dmsMapper;

    /**
     * Executes a DMS request and translates errors into {@link DmsException} instances.
     *
     * @param callable The DMS request to execute.
     * @param <T> The request result type.
     * @return The request result.
     */
    protected <T> T handleError(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final RestClientResponseException e) {
            DmsErrorResponse dmsError = null;
            try {
                dmsError = e.getResponseBodyAs(DmsErrorResponse.class);
            } catch (final RuntimeException ignored) {
            }
            final HttpStatus httpStatus = HttpStatus.resolve(e.getStatusCode().value());
            final String message = String.format(DMS_EXCEPTION_MESSAGE, e.getStatusCode(), dmsError);
            throw new DmsException(dmsMapper.toDomain(dmsError), httpStatus, message, e);
        } catch (final Exception e) {
            throw new DmsException("Dms request failed with unexpected error", e);
        }
    }
}

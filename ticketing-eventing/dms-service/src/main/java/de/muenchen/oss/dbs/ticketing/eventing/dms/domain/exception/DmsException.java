package de.muenchen.oss.dbs.ticketing.eventing.dms.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@SuppressWarnings("PMD.MissingSerialVersionUID")
@Getter
public class DmsException extends RuntimeException {
    private final DmsError dmsError;
    private final HttpStatus status;

    public DmsException(final String message, final Throwable cause) {
        this(null, null, message, cause);
    }

    public DmsException(final String message) {
        super(message);
        this.dmsError = null;
        this.status = null;
    }

    public DmsException(final DmsError dmsError, final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.dmsError = dmsError;
        this.status = status;
    }

    public record DmsError(
            Integer code,
            String message,
            String source) implements Serializable {
    }

    @RequiredArgsConstructor
    @Getter
    public enum DmsErrorCodes {
        OBJECT_ARCHIVED(14);

        private final int code;
    }
}

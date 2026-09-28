package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.ticketing;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ZammadApiException extends RuntimeException {
    public ZammadApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

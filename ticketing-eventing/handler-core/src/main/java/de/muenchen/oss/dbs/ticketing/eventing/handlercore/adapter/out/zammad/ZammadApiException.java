package de.muenchen.oss.dbs.ticketing.eventing.handlercore.adapter.out.zammad;

@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ZammadApiException extends RuntimeException {
    public ZammadApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.exception;

import java.io.Serial;

public class NoValidArticleException extends Exception {
    // Default access modifier is private
    @Serial
    private static final long serialVersionUID = 1L;

    public NoValidArticleException(final String s) {
        super(s);
    }
}

package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.dms;

import lombok.EqualsAndHashCode;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * Custom implementation of {@link AbstractResource} based on {@link InputStreamResource}.
 * Adds/overwrites implementations for {@link AbstractResource#contentLength()} and
 * {@link AbstractResource#getFilename()}.
 */
@EqualsAndHashCode(callSuper = true)
public class NamedInputStreamResource extends InputStreamResource {
    private final String filename;

    public NamedInputStreamResource(final String filename, final InputStream inputStream) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * Return unknown content length as not known for InputStream.
     * See java doc of {@link InputStreamResource} and {@link AbstractResource#contentLength()}.
     *
     * @return Unknown input length.
     */
    @Override
    public long contentLength() {
        return -1;
    }
}

package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.activation.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * DataSource from InputStream which stores InputStream into tmp file for multiple reads.
 */
@Slf4j
public class TempFileInputStreamDataSource implements DataSource {
    private final String name;
    private final String contentType;
    @Getter
    private final File tempFile;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    public TempFileInputStreamDataSource(final String name, final String contentType, final InputStream inputStream) throws IOException {
        this.name = name;
        this.contentType = contentType;
        final File tmpFile = Files.createTempFile("attachment", null).toFile();
        Files.copy(inputStream, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        this.tempFile = tmpFile;
        log.debug("Stored attachment {} in temp file {}", name, tmpFile.getName());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(tempFile);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("cannot do this");
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import jakarta.activation.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.Getter;

/**
 * DataSource from InputStream which stores InputStream into tmp file for multiple reads.
 */
public class TempFileInputStreamDataSource implements DataSource {
    private final String name;
    private final String contentType;
    @Getter
    private final File tempFile;

    public TempFileInputStreamDataSource(final String name, final String contentType, final InputStream inputStream) throws IOException {
        this.name = name;
        this.contentType = contentType;
        final File tmpFile = File.createTempFile("attachment", name);
        Files.copy(inputStream, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        this.tempFile = tmpFile;
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

package de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.InputStream;

/**
 * Properties for updating a ContentObject.
 *
 * @param name The name of the ContentObject including the file extension.
 * @param inputStream The content of the new ContentObject.
 */
public record DmsContentObjectRequest(@NotBlank String name, @NotNull InputStream inputStream) {
    /**
     * Get the name without the file extension.
     *
     * @return The name without the file extension.
     */
    public String getNameWithoutExtension() {
        final int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return name;
        }
        return name.substring(0, lastDotIndex);
    }
}

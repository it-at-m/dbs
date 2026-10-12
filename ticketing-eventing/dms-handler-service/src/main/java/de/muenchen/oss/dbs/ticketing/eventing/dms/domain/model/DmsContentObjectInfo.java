package de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Infos about an DMS ContentObject.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DmsContentObjectInfo {
    /**
     * COO of the ContentObject.
     */
    private String coo;
    /**
     * Filename of the ContentObject.
     */
    private String filename;
    /**
     * Fileextension of the ContentObject.
     */
    private String fileextension;
}

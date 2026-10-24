package de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Context for an DMS request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DmsRequestContext {
    /**
     * User under which a DMS request is executed.
     */
    private String username;
    /**
     * Used to resolve user role under which the DMS request is executed, default role if not defined.
     */
    private String joboe;
    /**
     * Used to resolve user role under which the DMS request is executed, default role if not defined.
     */
    private String jobposition;
}

package de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out;

import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface DmsOutPort {
    /**
     * Get the list of ContentObjects of the Incoming.
     *
     * @param dmsTarget The Incoming to search in.
     * @return The list of ContentObjects of the Incoming. Null if it doesn't exist.
     */
    List<DmsContentObjectInfo> getIncomingContentObjects(@NotNull @Valid DmsTarget dmsTarget);

    /**
     * Add ContentObjects to existing Incoming.
     *
     * @param dmsTarget The Incoming to add the ContentObjects to.
     * @param contentObjectRequests The files to add as ContentObjects.
     */
    void addContentObjectsToIncoming(@NotNull @Valid DmsTarget dmsTarget, @NotEmpty List<@Valid DmsContentObjectRequest> contentObjectRequests);

    /**
     * Update ContentObject.
     *
     * @param dmsTarget The target ContentObject.
     * @param contentObjectRequest The values and content of the updated ContentObject.
     */
    void updateContentObject(@NotNull @Valid DmsTarget dmsTarget, @NotNull @Valid DmsContentObjectRequest contentObjectRequest);

}

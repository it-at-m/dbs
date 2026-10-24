package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.dms;

import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.DmsOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.exception.DmsException;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectInfo;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectRequest;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsTarget;
import de.muenchen.oss.eakte.v1.generated.api.ContentObjectsApi;
import de.muenchen.oss.eakte.v1.generated.api.IncomingsApi;
import de.muenchen.oss.eakte.v1.generated.model.MetadataReferenz;
import de.muenchen.oss.eakte.v1.generated.model.ReadIncomingAntwortDTO;
import de.muenchen.oss.eakte.v1.generated.model.UpdateContentObjectAntwortDTO;
import de.muenchen.oss.eakte.v1.generated.model.UpdateContentObjectDTO;
import de.muenchen.oss.eakte.v1.generated.model.UpdateIncomingAnfrageDTO;
import de.muenchen.oss.eakte.v1.generated.model.UpdateIncomingAntwortDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmsAdapter implements DmsOutPort {
    private final DmsErrorHandler errorHandler;
    private final ContentObjectsApi contentObjectsApi;
    private final IncomingsApi incomingsApi;

    //TODO Naming
    private static final String DMS_APPLICATION = "TICKETING";

    @Override
    public List<DmsContentObjectInfo> getIncomingContentObjects(final DmsTarget dmsTarget) {
        final ReadIncomingAntwortDTO response = errorHandler.handleError(() -> incomingsApi.readIncoming(
                dmsTarget.getUsername(),
                dmsTarget.getCoo(),
                DMS_APPLICATION,
                dmsTarget.getJoboe(),
                dmsTarget.getJobposition()));
        if (response != null) {
            final List<MetadataReferenz> contentObjects = response.getGimetadatatype();
            log.info("Found ContentObjects {} for {}", contentObjects, dmsTarget);
            if (contentObjects == null) {
                return List.of();
            }
            return contentObjects.stream()
                    .map(i -> new DmsContentObjectInfo(i.getObjaddress(), i.getFilename(), i.getFileextension())).toList();
        } else {
            throw new DmsException("Response null while looking up ContentObjects of Incoming");
        }
    }

    @Override
    public void addContentObjectsToIncoming(final DmsTarget dmsTarget, final List<DmsContentObjectRequest> contentObjectRequests) {
        log.debug("Updating Incoming {} with {} files", dmsTarget, contentObjectRequests.size());
        final UpdateIncomingAnfrageDTO request = new UpdateIncomingAnfrageDTO();
        final List<Resource> attachments = contentObjectRequests.stream()
                .map(i -> new NamedInputStreamResource(i.name(), i.inputStream())).collect(Collectors.toList());
        final UpdateIncomingAntwortDTO response = errorHandler.handleError(() -> incomingsApi.updateIncoming(
                dmsTarget.getUsername(),
                dmsTarget.getCoo(),
                request,
                DMS_APPLICATION,
                dmsTarget.getJoboe(),
                dmsTarget.getJobposition(),
                attachments));
        if (response != null) {
            log.info("Updated Incoming {} by adding {} files", dmsTarget, contentObjectRequests.size());
        } else {
            throw new DmsException("Response null while updating Incoming");
        }
    }

    @Override
    public void updateContentObject(final DmsTarget dmsTarget, final DmsContentObjectRequest contentObjectRequest) {
        log.debug("Updating ContentObject {} ", dmsTarget);
        final UpdateContentObjectDTO request = new UpdateContentObjectDTO();
        final NamedInputStreamResource attachment = new NamedInputStreamResource(contentObjectRequest.name(), contentObjectRequest.inputStream());
        final UpdateContentObjectAntwortDTO response = errorHandler.handleError(() -> contentObjectsApi.updateContentObject(
                dmsTarget.getUsername(),
                dmsTarget.getCoo(),
                attachment,
                DMS_APPLICATION,
                dmsTarget.getJoboe(),
                dmsTarget.getJobposition(),
                request));
        if (response != null) {
            log.info("Updated ContentObject {}", dmsTarget);
        } else {
            throw new DmsException("Response null while updating ContentObject");
        }
    }
}

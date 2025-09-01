package de.muenchen.dbs.personalization.servicefinder;

import de.muenchen.dbs.personalization.common.NotFoundException;
import java.time.ZonedDateTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class ServiceFinderService {

    private final ServiceFinderRepository serviceFinderRepository;

    public ServiceFinder getService(String serviceID) {
        return serviceFinderRepository.findById(serviceID)
                .orElseThrow(() -> new NotFoundException("Service nicht gefunden"));
    }

    public ServiceFinder createService(ServiceFinder serviceFinder) {
        return serviceFinderRepository.save(serviceFinder);
    }

    public ServiceFinder updateService(String serviceID, ServiceFinder serviceFinder) {
        serviceFinderRepository.findById(serviceID)
                .orElseThrow(() -> new NotFoundException("Service nicht gefunden"));
        serviceFinder.setServiceID(serviceID);
        serviceFinder.setLastUpdate(ZonedDateTime.now());
        return serviceFinderRepository.save(serviceFinder);
    }

}

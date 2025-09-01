package de.muenchen.dbs.personalization.servicefinder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicefinder")
@Slf4j
@RequiredArgsConstructor
public class ServiceFinderController {

    private final ServiceFinderService serviceFinderService;

    @GetMapping("/{serviceID}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceFinder getServiceFinder(@PathVariable("serviceID") String serviceID) {
        return serviceFinderService.getService(serviceID);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceFinder createServiceFinder(@RequestBody ServiceFinder serviceFinder) {
        return serviceFinderService.createService(serviceFinder);
    }

    @PutMapping("/{serviceID}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceFinder updateServiceFinder(@PathVariable String serviceID, @RequestBody ServiceFinder serviceFinder) {
        return serviceFinderService.updateService(serviceID,serviceFinder);
    }
}

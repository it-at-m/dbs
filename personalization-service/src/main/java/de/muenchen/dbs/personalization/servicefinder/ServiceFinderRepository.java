package de.muenchen.dbs.personalization.servicefinder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceFinderRepository extends JpaRepository<ServiceFinder, String> {
}

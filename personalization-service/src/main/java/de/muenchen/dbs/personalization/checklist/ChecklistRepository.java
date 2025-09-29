package de.muenchen.dbs.personalization.checklist;

import de.muenchen.dbs.personalization.checklist.domain.Checklist;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {

    List<Checklist> findChecklistByLhmExtIdOrderByLastUpdateDesc(String lhmExtId);

}

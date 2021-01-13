package fr.abes.lnevent.repository;

import fr.abes.lnevent.repository.entities.EventRow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<EventRow, String> {
}

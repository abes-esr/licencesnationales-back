package fr.abes.lnevent.collection.repository;

import fr.abes.lnevent.collection.EventCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<EventCollection, String> {
}

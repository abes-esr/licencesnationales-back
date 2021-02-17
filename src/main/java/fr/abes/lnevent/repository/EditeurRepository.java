package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.EditeurRow;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EditeurRepository extends MongoRepository<EditeurRow, String> {
    void deleteById(String id);
}

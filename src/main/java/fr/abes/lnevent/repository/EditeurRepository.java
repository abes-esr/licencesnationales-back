package fr.abes.lnevent.repository;

import fr.abes.lnevent.repository.entities.EditeurRow;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EditeurRepository extends MongoRepository<EditeurRow, String> {
    void deleteById(String id);
}

package fr.abes.lnevent.repository;

import fr.abes.lnevent.repository.entities.ContactRow;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<ContactRow, String> {
    void deleteBySiren(String siren);
}

package fr.abes.lnevent.repository;

import fr.abes.lnevent.repository.entities.EtablissementRow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtablissementRepository extends MongoRepository<EtablissementRow, String> {
    void deleteBySiren(String siren);
}

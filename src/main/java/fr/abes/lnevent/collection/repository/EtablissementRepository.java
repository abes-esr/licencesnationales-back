package fr.abes.lnevent.collection.repository;

import fr.abes.lnevent.collection.EtablissementCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtablissementRepository extends MongoRepository<EtablissementCollection, String> {

    void deleteByName(String name);
}

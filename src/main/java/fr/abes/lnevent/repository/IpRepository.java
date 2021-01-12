package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.IpRow;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IpRepository extends MongoRepository<IpRow, String> {

    List<IpRow> findAllBySiren(String siren);
}

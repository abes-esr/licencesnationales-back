package fr.abes.lnevent.repository;

import fr.abes.lnevent.repository.entities.IpRow;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IpRepository extends MongoRepository<IpRow, String> {

    List<IpRow> findAllBySiren(String siren);
    void deleteByIpAndSiren(String ip, String siren);
}

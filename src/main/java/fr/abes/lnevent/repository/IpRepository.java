package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpRepository extends JpaRepository<IpEntity, String> {

    List<IpEntity> findAllBySiren(String siren);
    void deleteByIpAndSiren(String ip, String siren);
}

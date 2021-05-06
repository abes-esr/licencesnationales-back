package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IpRepository extends JpaRepository<IpEntity, Long> {

    @Query("select etab.ips from EtablissementEntity etab where etab.siren = ?1")
    Set<IpEntity> findAllBySiren(String siren);

    void deleteById(Long id);

    IpEntity getFirstById(Long id);
}

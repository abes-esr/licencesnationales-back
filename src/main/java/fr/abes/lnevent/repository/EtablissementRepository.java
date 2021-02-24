package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.EtablissementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, String> {
    void deleteBySiren(String siren);
}

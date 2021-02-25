package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.EtablissementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, String> {
    void deleteBySiren(String siren);

    @Query("select e.name from EtablissementEntity e where e.siren like :x")
    public String getNomEtabBySiren(@Param("x") String siren);
}

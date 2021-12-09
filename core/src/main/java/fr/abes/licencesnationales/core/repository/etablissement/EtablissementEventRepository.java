package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtablissementEventRepository extends JpaRepository<EtablissementEventEntity, Integer> {
    List<EtablissementEventEntity> getAllByDateCreationEventBetweenOrderByDateCreationEvent(Date dateDebut, Date dateFin);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementCreeEventEntity and e.siren = :siren")
    Optional<EtablissementEventEntity> getDateCreationEtab(@Param("siren") String siren);
}

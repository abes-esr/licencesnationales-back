package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EtablissementEventRepository extends JpaRepository<EtablissementEventEntity, Integer> {
    List<EtablissementEventEntity> getAllByDateCreationEventBetweenOrderByDateCreationEvent(Date dateDebut, Date dateFin);


}

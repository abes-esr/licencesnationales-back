package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EtablissementEventRepository extends JpaRepository<EtablissementEventEntity, Long> {
    List<EtablissementEventEntity> getAllByDateCreationEventBetweenOrderByDateCreationEvent(Date dateDebut, Date dateFin);
}

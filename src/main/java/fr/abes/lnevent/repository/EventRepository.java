package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> getAllByDateCreationEventBetweenOrderByDateCreationEvent(Date dateDebut, Date dateFin);
}

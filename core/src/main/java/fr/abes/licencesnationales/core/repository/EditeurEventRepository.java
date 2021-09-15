package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.editeur.event.EditeurEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditeurEventRepository extends JpaRepository<EditeurEventEntity, Integer> {
}
